package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import kotlinx.serialization.Serializable
import ru.maeasoftworks.normativecontrol.api.shared.modules.InstantSerializer
import java.time.Instant

@Serializable
data class Session(
    val userAgent: String?,
    @Serializable(InstantSerializer::class)
    val created: Instant
)