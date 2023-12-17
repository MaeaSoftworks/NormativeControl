package ru.maeasoftworks.normativecontrol.core.model

import ru.maeasoftworks.normativecontrol.core.enums.Closure
import ru.maeasoftworks.normativecontrol.core.enums.MistakeType

data class Mistake(
    val mistakeType: MistakeType,
    val closure: Closure,
    val actual: String? = null,
    val expected: String? = null
)