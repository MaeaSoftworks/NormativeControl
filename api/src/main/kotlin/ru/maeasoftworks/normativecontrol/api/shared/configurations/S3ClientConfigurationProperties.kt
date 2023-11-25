package ru.maeasoftworks.normativecontrol.api.shared.configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import software.amazon.awssdk.regions.Region
import java.net.URI

@ConfigurationProperties(prefix = "aws.s3")
class S3ClientConfigurationProperties {
    lateinit var region: Region
    lateinit var endpoint: URI
    lateinit var accessKeyId: String
    lateinit var secretAccessKey: String
    lateinit var bucket: String
    val multipartMinPartSize = 5 * 1024 * 1024
}
