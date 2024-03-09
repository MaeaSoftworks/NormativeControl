package normativecontrol.launcher.client

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import kotlinx.serialization.json.Json
import normativecontrol.core.Document
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUProfile
import normativecontrol.launcher.client.messages.JobMessage
import normativecontrol.launcher.client.messages.okResult
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class JobLauncher(channel: Channel): DefaultConsumer(channel) {
    override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: ByteArray?) {
        val jobId = properties.correlationId
        val stringBody = String(body!!)
        logger.debug("Received job $jobId with body: $stringBody")
        val message = try {
            Json.decodeFromString<JobMessage>(stringBody)
        } catch (e: Exception) {
            logger.warn("Job $jobId: unrecognized message body: $stringBody")
            return
        }
        val document = S3.getObject(message.document)
        Document(VerificationContext(UrFUProfile)).apply {
            load(ByteArrayInputStream(document))
            runVerification()
            val result = ByteArrayOutputStream()
            writeResult(result)
            S3.putObject(result.toByteArray(), message.resultDocx)
            S3.putObject(ctx.render.getString().toByteArray(), message.resultHtml)
        }
        Amqp.send(message.replyTo, properties.correlationId, okResult)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}