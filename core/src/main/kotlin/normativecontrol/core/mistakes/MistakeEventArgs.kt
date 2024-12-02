package normativecontrol.core.mistakes

import java.math.BigInteger

/**
 * Event args data class for [normativecontrol.core.contexts.VerificationContext.onMistakeEvent].
 * @property mistakeReason Mistake description
 * @property id Mistake unique id
 * @property expected Expected value
 * @property actual Actual value
 */
data class MistakeEventArgs(
    val mistakeReason: MistakeReason,
    val id: BigInteger,
    val expected: String? = null,
    val actual: String? = null
)
