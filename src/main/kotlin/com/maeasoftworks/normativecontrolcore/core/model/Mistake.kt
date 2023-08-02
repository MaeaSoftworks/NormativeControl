package com.maeasoftworks.normativecontrolcore.core.model

import com.maeasoftworks.normativecontrolcore.core.enums.CaptureType
import com.maeasoftworks.normativecontrolcore.core.enums.MistakeType

data class Mistake(
    val mistakeType: MistakeType,
    val captureType: CaptureType,
    val actual: String? = null,
    val expected: String? = null
)
