package bootstrapper.configurations

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration

@Configuration
@EnableConfigurationProperties(S3Properties::class)
class S3ClientConfiguration {
    @Bean
    fun s3Client(s3props: S3Properties): S3Client {
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