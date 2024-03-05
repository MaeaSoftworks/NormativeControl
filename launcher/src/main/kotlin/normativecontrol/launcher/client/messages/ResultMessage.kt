package normativecontrol.launcher.client.messages

import kotlinx.serialization.Serializable

@Serializable
data class ResultMessage(
    val result: String
)

val okResult = ResultMessage("OK")