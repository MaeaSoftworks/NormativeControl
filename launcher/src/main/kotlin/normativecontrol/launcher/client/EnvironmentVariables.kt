package normativecontrol.launcher.client

enum class EnvironmentVariables(private val path: String) {
    AMQP_URL("nc_amqp_url"),
    AMQP_QUEUE_NAME("nc_amqp_queue_name"),

    S3_REGION("nc_s3_region"),
    S3_ENDPOINT("nc_s3_endpoint"),
    S3_ACCESS_KEY_ID("nc_s3_access_key_id"),
    S3_SECRET_KEY_ID("nc_s3_secret_key_id"),
    S3_BUCKET("nc_s3_bucket");

    fun get(): String? {
        return System.getenv()[this.path]
    }
}