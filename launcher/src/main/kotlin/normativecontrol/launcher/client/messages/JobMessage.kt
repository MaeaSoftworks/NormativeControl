package normativecontrol.launcher.client.messages

import kotlinx.serialization.Serializable

@Serializable
data class JobMessage(
    val document: String,
    val replyTo: String,
    val resultDocx: String,
    val resultHtml: String
)