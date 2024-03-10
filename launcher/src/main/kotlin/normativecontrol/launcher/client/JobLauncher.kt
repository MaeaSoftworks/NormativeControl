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
import normativecontrol.launcher.client.messages.ResultMessage
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
        val source = S3.getObject(message.document)
        val document = Document(VerificationContext(UrFUProfile))
        document.load(ByteArrayInputStream(source))

        try {
            document.runVerification()
        } catch (e: Exception) {
            Amqp.send(message.replyTo, properties.correlationId, ResultMessage("ERROR", "Error during document verification"))
            return
        }

        try {
            val result = ByteArrayOutputStream()
            document.writeResult(result)
            S3.putObject(result.toByteArray(), message.resultDocx)
            S3.putObject(document.ctx.render.getString().toByteArray(), message.resultHtml)
        } catch (e: Exception) {
            Amqp.send(message.replyTo, properties.correlationId, ResultMessage("ERROR", "Saving or uploading error"))
            return
        }

        Amqp.send(message.replyTo, properties.correlationId, okResult)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}