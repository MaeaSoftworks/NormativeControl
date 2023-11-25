package ru.maeasoftworks.normativecontrol.api.shared.implementations

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.maeasoftworks.normativecontrol.api.shared.asFlow
import ru.maeasoftworks.normativecontrol.api.shared.await
import ru.maeasoftworks.normativecontrol.api.shared.configurations.S3ClientConfigurationProperties
import ru.maeasoftworks.normativecontrol.api.shared.exceptions.NotFoundException
import ru.maeasoftworks.normativecontrol.api.students.components.S3AsyncStorage
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import java.nio.ByteBuffer

@Component
class S3AsyncStorageImpl(private val s3Client: S3AsyncClient, private val s3props: S3ClientConfigurationProperties) : S3AsyncStorage {
    override suspend fun putObjectAsync(body: FilePart, objectName: String, tags: Map<String, String>) {
        val uploadState = UploadState(s3props.bucket, objectName)
        val result = s3Client
            .createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                    .contentType((body.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM).toString())
                    .key(objectName)
                    .tagging(Tagging.builder().tagSet(tags.map { Tag.builder().key(it.key).value(it.value).build() }).build())
                    .bucket(s3props.bucket)
                    .build()
            )
            .await()

        uploadState.uploadId = result.uploadId()

        Flux.from(body.content()).bufferUntil {
            uploadState.buffered += it.readableByteCount()
            return@bufferUntil if (uploadState.buffered >= s3props.multipartMinPartSize) {
                uploadState.buffered = 0
                true
            } else {
                false
            }
        }.map {
            concatBuffers(it)
        }.flatMap {
            uploadPart(uploadState, it)
        }.reduce(uploadState) { state, completedPart ->
            state.also { it.completedParts[completedPart.partNumber()] = completedPart }
        }.flatMap {
            completeUpload(it)
        }.asFlow().collect()
    }

    private fun concatBuffers(buffers: List<DataBuffer>): ByteBuffer {
        var partSize = 0
        buffers.forEach { partSize += it.readableByteCount() }
        val partData = ByteBuffer.allocate(partSize)
        buffers.forEach { it.toByteBuffer(partData) }
        partData.rewind()
        return partData
    }

    private fun uploadPart(uploadState: UploadState, buffer: ByteBuffer): Mono<CompletedPart> {
        val partNumber = ++uploadState.partCounter
        return Mono.fromFuture(
            s3Client.uploadPart(
                UploadPartRequest.builder()
                    .bucket(uploadState.bucket)
                    .key(uploadState.objectName)
                    .partNumber(partNumber)
                    .uploadId(uploadState.uploadId)
                    .contentLength(buffer.capacity().toLong())
                    .build(),
                AsyncRequestBody.fromPublisher(Mono.just(buffer))
            )
        ).map {
            CompletedPart.builder()
                .eTag(it.eTag())
                .partNumber(partNumber)
                .build()
        }
    }

    private fun completeUpload(state: UploadState): Mono<CompleteMultipartUploadResponse> {
        return Mono.fromFuture(
            s3Client.completeMultipartUpload(
                CompleteMultipartUploadRequest.builder()
                    .bucket(state.bucket)
                    .uploadId(state.uploadId)
                    .multipartUpload(
                        CompletedMultipartUpload.builder()
                            .parts(state.completedParts.values)
                            .build()
                    )
                    .key(state.objectName)
                    .build()
            )
        )
    }

    override fun getTagsAsync(objectName: String): Flow<Map<String, String>> {
        return s3Client.getObjectTagging(
            GetObjectTaggingRequest
                .builder()
                .bucket(s3props.bucket)
                .key(objectName)
                .build()
        ).asFlow().catch { // todo find cause
            throw NotFoundException("Object not found")
        }.map { tagSet ->
            tagSet.tagSet().associate { it.key() to it.value() }
        }
    }

    @OptIn(FlowPreview::class)
    override fun getObjectAsync(objectName: String): Flow<ByteBuffer> {
        return s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(s3props.bucket)
                .key(objectName)
                .build(),
            AsyncResponseTransformer.toPublisher()
        ).asFlow().map {
            it.asFlow()
        }.flatMapConcat {
            it
        }.catch {
            if (it is NoSuchKeyException) throw NotFoundException("Document not found")
        }
    }

    class UploadState(val bucket: String, val objectName: String) {
        var uploadId: String? = null
        var partCounter = 0
        var completedParts: MutableMap<Int, CompletedPart> = HashMap()
        var buffered = 0
    }
}
