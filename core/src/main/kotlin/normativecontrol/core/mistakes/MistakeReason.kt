package normativecontrol.core.mistakes

import kotlinx.serialization.Serializable

/**
 * Base interface for any mistakes.
 * @sample normativecontrol.implementation.urfu.Reason
 */
@Serializable(MistakeReasonSerializer::class)
interface MistakeReason {
    val id: Int
    val description: String
}