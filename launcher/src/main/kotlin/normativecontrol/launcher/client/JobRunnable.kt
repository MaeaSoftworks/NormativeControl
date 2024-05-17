package normativecontrol.launcher.client

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import normativecontrol.core.Core
import normativecontrol.core.data.Statistics
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.launcher.client.components.Database
import normativecontrol.launcher.client.components.S3
import normativecontrol.launcher.client.entities.Result
import normativecontrol.launcher.client.entities.Status
import normativecontrol.launcher.client.messages.Job
import normativecontrol.shared.debug
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream

class JobRunnable(private val job: Job) : Runnable {
    override fun run() {
        logger.debug { "Received job '${job.id}' with body: ${Json.encodeToString(job)}" }

        val source = try {
            S3.getObject(job.source)
        } catch (_: Exception) {
            return job.sendResult(Status.ERROR, "Error during document downloading")
        }

        val results = try {
            Core.verify(ByteArrayInputStream(source), UrFUConfiguration.NAME)
        } catch (_: Exception) {
            return job.sendResult(Status.ERROR, "Error during document verification")
        }

        try {
            S3.putObject(results.docx.toByteArray(), job.results.docx)
            S3.putObject(results.html.toByteArray(), job.results.html)
        } catch (_: Exception) {
            return job.sendResult(Status.ERROR, "Error during document uploading")
        }

        job.sendResult(Status.OK, statistics =  results.statistics)
    }

    private fun Job.sendResult(status: Status, description: String? = null, statistics: Statistics? = null) {
        logger.debug {
            description.let {
                if (description != null) "Job ${id}: $status: $description"
                else "Job ${id}: $status"
            }
        }
        Database.updateResult(Result(documentId, status, description, statistics))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JobRunnable::class.java)
    }
}