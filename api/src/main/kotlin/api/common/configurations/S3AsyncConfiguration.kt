package api.common.configurations

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.time.Duration

@Configuration
@EnableConfigurationProperties(S3ClientConfigurationProperties::class)
class S3AsyncConfiguration {
    @Bean
    fun s3AsyncClient(s3props: S3ClientConfigurationProperties): S3AsyncClient {
        return S3AsyncClient
            .builder()
            .httpClient(
                NettyNioAsyncHttpClient
                    .builder()
                    .writeTimeout(Duration.ZERO)
                    .maxConcurrency(64)
                    .build()
            )
            .region(s3props.region)
            .credentialsProvider { AwsBasicCredentials.create(s3props.accessKeyId, s3props.secretAccessKey) }
            .endpointOverride(s3props.endpoint)
            .serviceConfiguration(
                S3Configuration
                    .builder()
                    .checksumValidationEnabled(false)
                    .chunkedEncodingEnabled(true)
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build()
    }

    @Bean
    fun s3Client(s3props: S3ClientConfigurationProperties): S3Client {
        return S3Client
            .builder()
            .region(s3props.region)
            .credentialsProvider { AwsBasicCredentials.create(s3props.accessKeyId, s3props.secretAccessKey) }
            .endpointOverride(s3props.endpoint)
            .serviceConfiguration(
                S3Configuration
                    .builder()
                    .checksumValidationEnabled(false)
                    .chunkedEncodingEnabled(true)
                    .pathStyleAccessEnabled(true)
                    .build()
            )
            .build()
    }
}