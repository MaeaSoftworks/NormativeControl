package normativecontrol.core.abstractions.mistakes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mistake(
    @SerialName("c")
    val code: Int,
    @SerialName("i")
    val id: String,
    @SerialName("e")
    val expected: String? = null,
    @SerialName("a")
    val actual: String? = null
)