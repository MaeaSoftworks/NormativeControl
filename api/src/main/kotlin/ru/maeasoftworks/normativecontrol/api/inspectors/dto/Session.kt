package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import kotlinx.serialization.Serializable
import ru.maeasoftworks.normativecontrol.api.shared.modules.InstantSerializer
import java.time.Instant

@Serializable
data class Session(val userAgent: String?, val created: @Serializable(InstantSerializer::class) Instant)