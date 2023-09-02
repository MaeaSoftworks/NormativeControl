package bootstrapper.adapters

import bootstrapper.configurations.S3Properties
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.Tag
import software.amazon.awssdk.services.s3.model.Tagging
import java.io.InputStream

@Component
class S3Adapter(private val s3Client: S3Client, private val s3props: S3Properties) {
    fun getObject(objectName: String, tags: MutableMap<String, String>): InputStream {
        val tags1 = s3Client.getObjectTagging(GetObjectTaggingRequest.builder().bucket(s3props.bucket).key(objectName).build())
        tags.putAll(tags1.tagSet().associate { it.key() to it.value() })
        return s3Client.getObject(GetObjectRequest.builder().bucket(s3props.bucket).key(objectName).build())
    }

    fun putObject(file: ByteArray, objectName: String, tags: Map<String, String> = mapOf()) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(s3props.bucket)
                .key(objectName)
                .tagging(Tagging.builder().tagSet(tags.map { Tag.builder().key(it.key).value(it.value).build() }).build())
                .build(),
            RequestBody.fromBytes(file)
        )
    }
}