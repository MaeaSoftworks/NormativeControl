package normativecontrol.launcher.client

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import normativecontrol.launcher.cli.environment.environment
import org.slf4j.LoggerFactory
import java.io.Closeable

object Amqp : Closeable {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val url: String? by environment.variable("nc_amqp_url")
    private val queueName: String? by environment.variable("nc_amqp_queue_name")

    private val connection: Connection
    private val channel: Channel

    init {
        ApplicationFinalizer.add(this)
        val factory = ConnectionFactory().apply { setUri(url) }
        connection = factory.newConnection()
        logger.info("AMQP connected to ${factory.host}:${factory.port}")
        channel = connection.createChannel()
    }

    fun send(queueName: String, correlationId: String, body: String) {
        channel.basicPublish(
            "",
            queueName,
            AMQP.BasicProperties.Builder().correlationId(correlationId).build(),
            body.toByteArray()
        )
    }

    inline fun <reified T> send(queueName: String, correlationId: String, body: @Serializable T) = send(queueName, correlationId, Json.encodeToString(body))

    fun listen() {
        channel.queueDeclare(queueName, true, false, false, null)
        channel.basicConsume(queueName, true, JobConsumer(channel))
    }

    override fun close() {
        channel.close()
        connection.close()
        logger.info("AMQP connection closed")
    }
}