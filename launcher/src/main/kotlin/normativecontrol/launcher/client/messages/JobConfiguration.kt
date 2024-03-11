package normativecontrol.launcher.client.messages

import kotlinx.serialization.Serializable

@Serializable
data class JobConfiguration(
    val document: String,
    val replyTo: String,
    val resultDocx: String,
    val resultHtml: String
)