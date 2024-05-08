package normativecontrol.core.mistakes

import kotlinx.serialization.Serializable

@Serializable(MistakeReasonSerializer::class)
interface MistakeReason {
    val id: Int
    val description: String
}