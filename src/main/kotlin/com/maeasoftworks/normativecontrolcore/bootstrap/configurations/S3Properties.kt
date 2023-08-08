package com.maeasoftworks.normativecontrolcore.bootstrap.configurations

import org.springframework.boot.context.properties.ConfigurationProperties
import software.amazon.awssdk.regions.Region
import java.net.URI

@ConfigurationProperties(prefix = "aws.s3")
class S3Properties {
    lateinit var region: Region
    lateinit var endpoint: URI
    lateinit var accessKeyId: String
    lateinit var secretAccessKey: String
    lateinit var bucket: String
}