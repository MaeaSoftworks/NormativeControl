package normativecontrol.launcher.client.entities

import normativecontrol.core.data.Statistics

data class Result(
    val id: Long,
    val status: Status,
    val description: String?,
    val statistics: Statistics?
)