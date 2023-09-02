package core.model

import core.enums.CaptureType
import core.enums.MistakeType

data class Mistake(
    val mistakeType: MistakeType,
    val captureType: CaptureType,
    val actual: String? = null,
    val expected: String? = null
)
