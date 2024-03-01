package normativecontrol.core.abstractions.mistakes

data class Mistake(
    val mistakeReason: MistakeReason,
    val actual: String? = null,
    val expected: String? = null
)