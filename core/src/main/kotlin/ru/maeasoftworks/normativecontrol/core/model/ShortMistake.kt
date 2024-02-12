package ru.maeasoftworks.normativecontrol.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ShortMistake(
    val code: Int,
    val id: String,
    val expected: String? = null,
    val actual: String? = null
)