package normativecontrol.core.mistakes

import java.math.BigInteger

/**
 * Event args data class for [normativecontrol.core.contexts.VerificationContext.onMistakeEvent].
 * @param mistakeReason mistake description
 * @param id mistake unique id
 * @param expected expected value
 * @param actual actual value
 */
data class MistakeEventArgs(
    val mistakeReason: MistakeReason,
    val id: BigInteger,
    val expected: String? = null,
    val actual: String? = null
)
