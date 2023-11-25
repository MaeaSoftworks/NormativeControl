package ru.maeasoftworks.normativecontrol.shared.modules

import io.ktor.server.application.Application
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
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

class S3(application: Application) {
    private val region = Region.of(application.environment.config.property("aws.s3.region").getString())
    private val endpoint = URI(application.environment.config.property("aws.s3.endpoint").getString())
    private val accessKeyId = application.environment.config.property("aws.s3.accessKeyId").getString()
    private val secretAccessKey = application.environment.config.property("aws.s3.secretAccessKey").getString()
    private val bucket = application.environment.config.property("aws.s3.bucket").getString()
    private val s3Client: S3AsyncClient = S3AsyncClient
        .builder()
        .httpClient(NettyNioAsyncHttpClient.builder().writeTimeout(Duration.ZERO).maxConcurrency(64).build())
        .region(region)
        .credentialsProvider { AwsBasicCredentials.create(accessKeyId, secretAccessKey) }
        .endpointOverride(endpoint)
        .serviceConfiguration(S3Configuration.builder().checksumValidationEnabled(false).chunkedEncodingEnabled(true).pathStyleAccessEnabled(true).build())
        .build()

    suspend fun putObject(file: ByteArray, objectName: String, tags: Map<String, String>): PutObjectResponse = coroutineScope {
        return@coroutineScope s3Client.putObject(
            PutObjectRequest.builder()
                .tagging(Tagging.builder().tagSet(tags.map { Tag.builder().key(it.key).value(it.value).build() }).build())
                .key(objectName)
                .bucket(bucket)
                .build(),
            AsyncRequestBody.fromBytes(file)
        ).await()
    }

    suspend fun getTags(objectName: String): Map<String, String> = coroutineScope {
        return@coroutineScope s3Client.getObjectTagging(
            GetObjectTaggingRequest
                .builder()
                .bucket(bucket)
                .key(objectName)
                .build()
        ).await().let { tagSet -> tagSet.tagSet().associate { it.key() to it.value() } }
    }

    suspend fun getObject(objectName: String): Flow<ByteBuffer> = coroutineScope {
        return@coroutineScope s3Client.getObject(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectName)
                .build(),
            AsyncResponseTransformer.toPublisher()
        ).await().asFlow()
    }
}