package ru.maeasoftworks.normativecontrol.api.students.components

import org.springframework.stereotype.Component
import ru.maeasoftworks.normativecontrol.api.shared.configurations.S3ClientConfigurationProperties
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.Tag
import software.amazon.awssdk.services.s3.model.Tagging

@Component
class S3Storage(private val s3Client: S3Client, private val s3props: S3ClientConfigurationProperties) {
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
