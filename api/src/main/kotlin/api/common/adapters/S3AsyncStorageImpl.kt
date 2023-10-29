package api.common.adapters

import api.common.configurations.S3ClientConfigurationProperties
import api.students.dto.UploadState
import api.common.exceptions.NotFoundException
import api.students.components.S3AsyncStorage
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.*
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture

@Component
class S3AsyncStorageImpl(private val s3Client: S3AsyncClient, private val s3props: S3ClientConfigurationProperties): S3AsyncStorage {
    override fun putObjectAsync(body: FilePart, objectName: String, tags: Map<String, String>): Mono<Boolean> {
        val uploadRequest = s3Client
            .createMultipartUpload(
                CreateMultipartUploadRequest.builder()
                    .contentType((body.headers().contentType ?: MediaType.APPLICATION_OCTET_STREAM).toString())
                    .key(objectName)
                    .tagging(Tagging.builder().tagSet(tags.map { Tag.builder().key(it.key).value(it.value).build() }).build())
                    .bucket(s3props.bucket)
                    .build()
            )

        val uploadState = UploadState(s3props.bucket, objectName)

        return Mono
            .fromFuture(uploadRequest)
            .flatMapMany {
                uploadState.uploadId = it.uploadId()
                Flux.from(body.content())
            }
            .bufferUntil {
                uploadState.buffered += it.readableByteCount()
                if (uploadState.buffered >= s3props.multipartMinPartSize) {
                    uploadState.buffered = 0
                    return@bufferUntil true
                } else {
                    return@bufferUntil false
                }
            }
            .map { concatBuffersAsync(it) }
            .flatMap { uploadPartAsync(uploadState, it) }
            .reduce(uploadState) { state, completedPart ->
                state.completedParts[completedPart.partNumber()] = completedPart
                state
            }
            .flatMap { completeUploadAsync(it) }
            .map {
                true
            }
    }

    private fun concatBuffersAsync(buffers: List<DataBuffer>): ByteBuffer {
        var partSize = 0
        buffers.forEach { partSize += it.readableByteCount() }
        val partData = ByteBuffer.allocate(partSize)
        buffers.forEach { it.toByteBuffer(partData) }
        partData.rewind()
        return partData
    }

    private fun uploadPartAsync(uploadState: UploadState, buffer: ByteBuffer): Mono<CompletedPart> {
        val partNumber = ++uploadState.partCounter
        val request: CompletableFuture<UploadPartResponse> = s3Client.uploadPart(
            UploadPartRequest.builder()
                .bucket(uploadState.bucket)
                .key(uploadState.objectName)
                .partNumber(partNumber)
                .uploadId(uploadState.uploadId)
                .contentLength(buffer.capacity().toLong())
                .build(),
            AsyncRequestBody.fromPublisher(Mono.just(buffer))
        )
        return Mono
            .fromFuture(request)
            .map {
                CompletedPart.builder()
                    .eTag(it.eTag())
                    .partNumber(partNumber)
                    .build()
            }
    }

    private fun completeUploadAsync(state: UploadState): Mono<CompleteMultipartUploadResponse> {
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

    override fun getTagsAsync(objectName: String): Mono<Map<String, String>> {
        return Mono
            .fromFuture(
                s3Client.getObjectTagging(
                    GetObjectTaggingRequest
                        .builder()
                        .bucket(s3props.bucket)
                        .key(objectName)
                        .build()
                )
            )
            .map { tagSet -> tagSet.tagSet().associate { it.key() to it.value() } }
            .doOnError { throw NotFoundException("Object not found") }
    }

    override fun getObjectAsync(objectName: String): Flux<ByteBuffer> {
        val request = GetObjectRequest.builder()
            .bucket(s3props.bucket)
            .key(objectName)
            .build()

        return Mono
            .fromFuture(s3Client.getObject(request, AsyncResponseTransformer.toPublisher()))
            .flatMapMany { it }
            .onErrorResume { e ->
                when (e) {
                    is NoSuchKeyException -> throw NotFoundException("Document not found")
                    else -> throw e
                }
            }
    }
}