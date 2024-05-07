package normativecontrol.launcher.client.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Suppress("DEPRECATION")
@Serializable
data class Job(
    val userId: Long?,
    @SerialName("id")
    val documentId: Long,
    @Deprecated("Use new json schema")
    @SerialName("source")
    val sourceDeprecated: String?,
    @Deprecated("Use new json schema")
    @SerialName("results")
    val resultsDeprecated: normativecontrol.launcher.client.messages.Results?
) {
    @Transient
    val id = "$userId/$documentId"

    @Transient
    val source = sourceDeprecated ?: "$id.source.docx"

    @Transient
    val results = Results(this)

    @JvmInline
    value class Results(private val job: Job) {
        val docx: String
            get() = job.resultsDeprecated?.docxDeprecated ?: "${job.id}.result.docx"

        val html: String
            get() = job.resultsDeprecated?.docxDeprecated ?: "${job.id}.result.html"
    }
}