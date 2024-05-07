package normativecontrol.launcher.client.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Deprecated("Use new json schema")
@Serializable
data class Results(
    @SerialName("docx")
    @Deprecated("Use new json schema")
    val docxDeprecated: String?,
    @SerialName("html")
    @Deprecated("Use new json schema")
    val htmlDeprecated: String?
)
