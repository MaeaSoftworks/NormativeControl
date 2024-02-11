package ru.maeasoftworks.normativecontrol.core.model

import kotlinx.serialization.Serializable
import ru.maeasoftworks.normativecontrol.core.abstractions.MistakeReason

@Serializable
data class DetailedMistake(
    val mistakeReason: MistakeReason,
    val description: String,
    val id: String
)