package normativecontrol.launcher.client

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import normativecontrol.core.Core
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.launcher.client.components.Database
import normativecontrol.launcher.client.components.S3
import normativecontrol.launcher.client.entities.Result
import normativecontrol.launcher.client.entities.Status
import normativecontrol.launcher.client.messages.Job
import normativecontrol.shared.*
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream

class JobRunnable(private val job: Job): Runnable {
    override fun run() {
        logger.debug { "Received job '${job.id}' with body: ${Json.encodeToString(job)}" }

        val source = try {
            S3.getObject(job.source)
        } catch (_: Exception) {
            job.sendResult(Status.ERROR, "Error during document downloading")
            return
        }

        val results = try {
            Core.verify(ByteArrayInputStream(source), UrFUConfiguration())
        } catch (_: Exception) {
            job.sendResult(Status.ERROR, "Error during document verification")
            return
        }

        try {
            S3.putObject(results.first.toByteArray(), job.results.docx)
            S3.putObject(results.second.toByteArray(), job.results.html)
        } catch (_: Exception) {
            job.sendResult(Status.ERROR, "Error during document uploading")
            return
        }

        job.sendResult(Status.OK)
    }

    private fun Job.sendResult(status: Status, description: String? = null) {
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