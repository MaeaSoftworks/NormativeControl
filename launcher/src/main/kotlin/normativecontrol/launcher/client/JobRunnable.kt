package normativecontrol.launcher.client

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import normativecontrol.core.Document
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.launcher.client.components.Database
import normativecontrol.launcher.client.components.S3
import normativecontrol.launcher.client.entities.Result
import normativecontrol.launcher.client.entities.Status
import normativecontrol.launcher.client.messages.Job
import normativecontrol.shared.*
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class JobRunnable(private val job: Job): Runnable {
    override fun run() = interruptable {
        logger.debug { "Received job '${job.id}' with body: ${Json.encodeToString(job)}" }

        val source = S3.getObject(job.source).interruptIfNullWith { job.interrupt(Status.ERROR, "Error during document downloading") }

        val document = Document(UrFUConfiguration)
        document.load(ByteArrayInputStream(source))

        interruptOnAnyException({ job.interrupt(Status.ERROR, "Error during document verification") }) {
            document.runVerification()
        }

        val result = ByteArrayOutputStream()
        interruptOnAnyException({ job.interrupt(Status.ERROR, "Error during document saving") }) {
            document.writeResult(result)
        }

        interruptOnAnyException({ job.interrupt(Status.ERROR, "Error during document uploading") }) {
            S3.putObject(result.toByteArray(), job.results.docx)
            S3.putObject(document.ctx.render.getString().toByteArray(), job.results.html)
        }

        job.reply(Status.OK)
    }

    private fun Job.interrupt(status: Status, description: String? = null) = interrupter {
        reply(status, description)
    }

    private fun Job.reply(status: Status, description: String? = null) {
        logger.debug {
            description.let {
                if (description != null) "Job ${id}: $status: $description"
                else "Job ${id}: $status"
            }
        }
        Database.updateResult(
            Result(
                id,
                status,
                description
            )
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobRunnable::class.java)
    }
}