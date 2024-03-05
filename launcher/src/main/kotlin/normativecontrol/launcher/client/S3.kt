package normativecontrol.launcher.client

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.model.*
import java.net.URI

object S3 {
    private lateinit var bucket: String
    private lateinit var s3Client: S3Client

    fun initialize() {
        val region = Region.of(EnvironmentVariables.S3_REGION.get() ?: throw NullPointerException("S3 region was not specified"))
        val endpoint = URI(EnvironmentVariables.S3_ENDPOINT.get() ?: throw NullPointerException("S3 endpoint was not specified"))
        val accessKeyId = EnvironmentVariables.S3_ACCESS_KEY_ID.get() ?: throw NullPointerException("S3 access key id was not specified")
        val secretAccessKey = EnvironmentVariables.S3_SECRET_KEY_ID.get() ?: throw NullPointerException("S3 secret access key was not specified")
        bucket = EnvironmentVariables.S3_BUCKET.get() ?: throw NullPointerException("S3 bucket was not specified")
        s3Client = S3Client
            .builder()
            .region(region)
            .credentialsProvider { AwsBasicCredentials.create(accessKeyId, secretAccessKey) }
            .endpointOverride(endpoint)
            .serviceConfiguration(
                S3Configuration.builder()
                    .checksumValidationEnabled(false)
                    .chunkedEncodingEnabled(true)
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build()
    }

    fun putObject(file: ByteArray, objectName: String, vararg tags: Pair<String, String>) {
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
            RequestBody.fromBytes(file)
        )
    }

    fun getTags(objectName: String): Map<String, String> {
        return s3Client.getObjectTagging(
            GetObjectTaggingRequest
                .builder()
                .bucket(bucket)
                .key(objectName)
                .build()
        ).let { tagSet -> tagSet.tagSet().associate { it.key() to it.value() } }
    }

    fun getObject(objectName: String): ByteArray? {
        return s3Client.getObjectAsBytes(
            GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectName)
                .build(),
        ).asByteArray()
    }
}