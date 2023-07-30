package com.maeasoftworks.bootstrap.configurations

import io.minio.MinioClient
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinIOConfiguration {
    @Value("\${minio.endpoint}")
    private lateinit var endpoint: String

    @Value("\${minio.bucket}")
    private lateinit var bucket: String

    @Value("\${minio.login}")
    private lateinit var login: String

    @Value("\${minio.password}")
    private lateinit var password: String

    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(login, password)
            .build()
    }

    @PostConstruct
    fun postConstruct() {
        ValueStorage.bucket = bucket
    }
}