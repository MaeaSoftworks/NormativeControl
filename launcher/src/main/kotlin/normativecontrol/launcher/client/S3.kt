package normativecontrol.launcher.client

import normativecontrol.launcher.cli.environment.environment
import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.model.*
import java.io.Closeable
import java.net.URI

object S3: Closeable {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val region: String by environment["nc_s3_region"]
    private val bucket: String by environment["nc_s3_bucket"]
    private val endpoint: String by environment["nc_s3_endpoint"]
    private val accessKeyId: String by environment["nc_s3_access_key_id"]
    private val secretAccessKey: String by environment["nc_s3_secret_key_id"]

    private val s3Client: S3Client

    init {
        ApplicationFinalizer.add(this)
        s3Client = S3Client
            .builder()
            .region(Region.of(region))
            .credentialsProvider { AwsBasicCredentials.create(accessKeyId, secretAccessKey) }
            .endpointOverride(URI(endpoint))
            .serviceConfiguration(
                S3Configuration.builder()
                    .checksumValidationEnabled(false)
                    .chunkedEncodingEnabled(true)
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build()
        logger.info("Connected S3 storage: [region: '$region', endpoint: '$endpoint']")
        if (!s3Client.listBuckets().buckets().any { it.name() == bucket }) {
            logger.info("S3 bucket '$bucket' not found, creating a new bucket with name '$bucket'...")
            s3Client.createBucket(
                CreateBucketRequest.builder()
                    .bucket(bucket)
                    .build()
            )
            logger.info("S3 bucket '$bucket' created!")
        }
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

    override fun close() {
        s3Client.close()
    }
}