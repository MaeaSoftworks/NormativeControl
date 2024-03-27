package normativecontrol.launcher.client.messages

import kotlinx.serialization.Serializable

@Serializable
data class Job(
    val id: Long,
    val source: String,
    val results: Results
)