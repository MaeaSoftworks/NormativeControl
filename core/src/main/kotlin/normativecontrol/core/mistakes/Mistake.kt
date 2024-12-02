package normativecontrol.core.mistakes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Serializable mistake representation which will be used in JavaScript generated code.
 * @property code [MistakeReason] code mapped by [MistakeSerializer]
 * @property id Mistake unique id
 * @property expected Expected value
 * @property actual Actual value
 */
@Serializable
internal data class Mistake(
    @SerialName("c")
    val code: Int,
    @SerialName("i")
    val id: String,
    @SerialName("e")
    val expected: String? = null,
    @SerialName("a")
    val actual: String? = null
)