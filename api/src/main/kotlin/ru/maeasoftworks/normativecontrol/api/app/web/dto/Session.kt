package ru.maeasoftworks.normativecontrol.api.app.web.dto

import kotlinx.serialization.Serializable
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.InstantSerializer
import java.time.Instant

@Serializable
data class Session(
    val userAgent: String?,
    @Serializable(InstantSerializer::class)
    val created: Instant
)