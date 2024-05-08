package normativecontrol.launcher.client.messages

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Job(
    val userId: Long?,
    val documentId: Long,
) {
    @Transient
    val id = "$userId/$documentId"

    @Transient
    val source = "$id.source.docx"

    @Transient
    val results = Results(this)

    @JvmInline
    value class Results(private val job: Job) {
        val docx: String
            get() = "${job.id}.result.docx"

        val html: String
            get() = "${job.id}.result.html"
    }
}