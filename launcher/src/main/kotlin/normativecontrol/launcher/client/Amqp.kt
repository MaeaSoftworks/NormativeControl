package normativecontrol.launcher.client

import com.rabbitmq.client.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.cli.MissingArgumentException
import org.slf4j.LoggerFactory

object Amqp {
    private lateinit var connection: Connection
    private lateinit var queueName: String
    lateinit var channel: Channel
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun initialize() {
        Runtime.getRuntime().addShutdownHook(Thread(::close))
        val uri = EnvironmentVariables.AMQP_URL.get() ?: throw MissingArgumentException("Client Mode requires amqp_url environment variable.")
        queueName = EnvironmentVariables.AMQP_QUEUE_NAME.get() ?: throw MissingArgumentException("Client Mode requires amqp_queue_name environment variable.")
        val factory = ConnectionFactory().apply {
            setUri(uri)
        }
        connection = factory.newConnection()
        logger.info("AMQP connected to ${factory.host}:${factory.port}")
        channel = connection.createChannel()
    }

    inline fun <reified T> send(queueName: String, correlationId: String, body: @Serializable T) {
        channel.basicPublish(
            "",
            queueName,
            AMQP.BasicProperties.Builder().correlationId(correlationId).build(),
            Json.encodeToString(body).toByteArray()
        )
    }

    fun listen() {
        channel.queueDeclare(queueName, true, false, false, null)
        channel.basicConsume(queueName, true, JobLauncher(channel))
    }

    private fun close() {
        logger.info("Shutting down...")
        connection.close()
    }
}