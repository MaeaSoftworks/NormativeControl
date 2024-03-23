package normativecontrol.launcher.client

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import normativecontrol.core.Document
import normativecontrol.core.implementations.ufru.UrFUProfile
import normativecontrol.launcher.client.messages.JobResult
import normativecontrol.launcher.utils.*
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class JobRunnable(private val jobData: JobData): Runnable {
    override fun run() = interruptable {
        logger.debug { "Received job '${jobData.properties.correlationId}' with body: ${Json.encodeToString(jobData.jobConfiguration)}" }

        val source = S3.getObject(jobData.source).interruptIfNullWith { jobData.interrupt("ERROR", "Error during document downloading") }

        val document = Document(UrFUProfile)
        document.load(ByteArrayInputStream(source))

        interruptOnAnyException({ jobData.interrupt("ERROR", "Error during document verification") }) {
            document.runVerification()
        }

        val result = ByteArrayOutputStream()
        interruptOnAnyException({ jobData.interrupt("ERROR", "Error during document saving") }) {
            document.writeResult(result)
        }

        interruptOnAnyException({ jobData.interrupt("ERROR", "Error during document uploading") }) {
            S3.putObject(result.toByteArray(), jobData.docxName)
            S3.putObject(document.ctx.render.getString().toByteArray(), jobData.htmlName)
        }

        jobData.reply("OK")
    }

    private fun JobData.interrupt(status: String, description: String? = null) = interrupter {
        reply(status, description)
    }

    private fun JobData.reply(status: String, description: String? = null) {
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