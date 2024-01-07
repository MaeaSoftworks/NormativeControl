package ru.maeasoftworks.normativecontrol.api.infrastructure.filestorage

import io.ktor.server.application.Application
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import ru.maeasoftworks.normativecontrol.api.infrastructure.utils.Module
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.model.*
import java.net.URI
import java.nio.ByteBuffer
import java.time.Duration

object S3FileStorage : Module, FileStorage {
    private lateinit var bucket: String
    private lateinit var s3Client: S3AsyncClient

    override fun Application.module() {
        FileStorage.initialize(S3FileStorage)
        val region = Region.of(environment.config.property("aws.s3.region").getString())
        val endpoint = URI(environment.config.property("aws.s3.endpoint").getString())
        val accessKeyId = environment.config.property("aws.s3.accessKeyId").getString()
        val secretAccessKey = environment.config.property("aws.s3.secretAccessKey").getString()
        bucket = environment.config.property("aws.s3.bucket").getString()
        s3Client = S3AsyncClient
            .builder()
            .httpClient(NettyNioAsyncHttpClient.builder().writeTimeout(Duration.ZERO).maxConcurrency(64).build())
            .region(region)
            .credentialsProvider { AwsBasicCredentials.create(accessKeyId, secretAccessKey) }
            .endpointOverride(endpoint)
            .serviceConfiguration(S3Configuration.builder().checksumValidationEnabled(false).chunkedEncodingEnabled(true).pathStyleAccessEnabled(true).build())
            .build()
    }

    override suspend fun putObject(file: ByteArray, objectName: String, vararg tags: Pair<String, String>): Unit = coroutineScope {
        s3Client.putObject(
            PutObjectRequest.builder()
                .tagging(
                    Tagging.builder()
                        .tagSet(tags.map { (key, value) -> Tag.builder().apply { key(key).value(value) }.build() })
                        .build()
                )
                .key(objectName)
                .bucket(bucket)
                .build(),
            AsyncRequestBody.fromBytes(file)
        ).await()
    }

    override suspend fun getTags(objectName: String): Map<String, String> = coroutineScope {
        return@coroutineScope s3Client.getObjectTagging(
            GetObjectTaggingRequest
                .builder()
                .bucket(bucket)
                .key(objectName)
                .build()
        ).await().let { tagSet -> tagSet.tagSet().associate { it.key() to it.value() } }
    }

    override suspend fun getObject(objectName: String): Flow<ByteBuffer> = coroutineScope {
        return@coroutineScope s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectName)
                .build(),
            AsyncResponseTransformer.toPublisher()
        ).await().asFlow()
    }
}