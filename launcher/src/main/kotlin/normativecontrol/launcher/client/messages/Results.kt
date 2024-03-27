package normativecontrol.launcher.client.messages

import kotlinx.serialization.Serializable

@Serializable
data class Results(
    val docx: String,
    val html: String
)
