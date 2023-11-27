package ru.maeasoftworks.normativecontrol.inspectors.dto

import kotlinx.serialization.Serializable
import ru.maeasoftworks.normativecontrol.shared.modules.InstantSerializer
import java.time.Instant

@Serializable
data class Session(val userAgent: String?, val created: @Serializable(InstantSerializer::class) Instant)