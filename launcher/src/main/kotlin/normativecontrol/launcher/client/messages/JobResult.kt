package normativecontrol.launcher.client.messages

import kotlinx.serialization.Serializable

@Serializable
data class JobResult(
    val status: String,
    val description: String? = null
)