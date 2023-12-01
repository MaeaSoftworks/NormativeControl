package ru.maeasoftworks.normativecontrol.core.model

import ru.maeasoftworks.normativecontrol.core.enums.CaptureType
import ru.maeasoftworks.normativecontrol.core.enums.MistakeType

data class Mistake(
    val mistakeType: MistakeType,
    val captureType: CaptureType,
    val actual: String? = null,
    val expected: String? = null
)