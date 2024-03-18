package normativecontrol.launcher.client

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import normativecontrol.core.Document
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUProfile
import normativecontrol.launcher.client.messages.JobResult
import normativecontrol.launcher.utils.*
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class JobConsumer(channel: Channel): DefaultConsumer(channel) {
    override fun handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties, body: ByteArray?) = proceedJob(Job(properties, body))

    private fun proceedJob(job: Job) = interruptable {
        logger.debug { "Received job '${job.properties.correlationId}' with body: ${ Json.encodeToString(job.jobConfiguration) }" }

        val source = S3.getObject(job.source).interruptIfNullWith { job.interrupt("ERROR", "Error during document downloading") }

        val document = Document(VerificationContext(UrFUProfile))
        document.load(ByteArrayInputStream(source))

        interruptOnAnyException({ job.interrupt("ERROR", "Error during document verification") }) {
            document.runVerification()
        }

        val result = ByteArrayOutputStream()
        interruptOnAnyException({ job.interrupt("ERROR", "Error during document saving") }) {
            document.writeResult(result)
        }

        interruptOnAnyException({ job.interrupt("ERROR", "Error during document uploading") }) {
            S3.putObject(result.toByteArray(), job.docxName)
            S3.putObject(document.ctx.render.getString().toByteArray(), job.htmlName)
        }

        job.reply("OK")
    }

    private fun Job.interrupt(status: String, description: String? = null) = interrupter {
        reply(status, description)
    }

    private fun Job.reply(status: String, description: String? = null)  {
        logger.debug {
            description.let {
                if (description != null) "Job ${properties.correlationId}: $status: $description"
                else "Job ${properties.correlationId}: $status"
            }
        }
        Amqp.send(replyTo, properties.correlationId, JobResult(status, description))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}