package normativecontrol.launcher.client.messages

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import normativecontrol.core.data.Statistics
import normativecontrol.core.locales.Locales
import normativecontrol.launcher.client.components.Database
import normativecontrol.launcher.client.entities.Result
import normativecontrol.launcher.client.entities.Status
import normativecontrol.shared.debug
import org.slf4j.LoggerFactory

@Serializable
data class Job(
    val userId: Long?,
    val documentId: Long,
    val locale: Locales = Locales.RU
) {
    @Transient
    val id = "$userId/$documentId"

    @Transient
    val source = "$id/source.docx"

    @Transient
    val results = Results(this)

    @JvmInline
    value class Results(private val job: Job) {
        val docx: String
            get() = "${job.id}/result.docx"

        val html: String
            get() = "${job.id}/result.html"
    }

    fun sendResult(status: Status, description: String? = null, statistics: Statistics? = null) {
        logger.debug {
            description.let {
                if (description != null) "Job ${id}: $status: $description"
                else "Job ${id}: $status"
            }
        }
        Database.updateResult(Result(documentId, status, description, statistics))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Job::class.java)
    }
}