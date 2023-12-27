package ru.maeasoftworks.normativecontrol.api.app.web.dto

import kotlinx.serialization.Serializable
import ru.maeasoftworks.normativecontrol.api.domain.dao.RefreshToken
import ru.maeasoftworks.normativecontrol.api.infrastructure.web.InstantSerializer
import java.time.Instant

@Serializable
data class RefreshTokenResponse(
    val refreshToken: String,
    @Serializable(InstantSerializer::class)
    val createdAt: Instant,
    @Serializable(InstantSerializer::class)
    val expiresAt: Instant
)

fun RefreshToken.asResponse(): RefreshTokenResponse {
    return RefreshTokenResponse(refreshToken, createdAt, expiresAt)
}