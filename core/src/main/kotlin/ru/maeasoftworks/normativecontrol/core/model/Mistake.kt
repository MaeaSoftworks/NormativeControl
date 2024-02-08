package ru.maeasoftworks.normativecontrol.core.model

import ru.maeasoftworks.normativecontrol.core.abstractions.Closure
import ru.maeasoftworks.normativecontrol.core.abstractions.MistakeReason

data class Mistake(
    val mistakeReason: MistakeReason,
    val closure: Closure,
    val actual: String? = null,
    val expected: String? = null
)