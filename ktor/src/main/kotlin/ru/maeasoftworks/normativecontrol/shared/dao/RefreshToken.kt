package ru.maeasoftworks.normativecontrol.shared.dao

import org.komapper.annotation.*
import java.time.Instant

@KomapperTable("refresh_tokens")
@KomapperEntity(["refreshTokens"])
@KomapperManyToOne(User::class)
data class RefreshToken(
    @KomapperId
    @KomapperAutoIncrement
    val id: Long = 0,
    val refreshToken: String,
    val createdAt: Instant,
    val expiresAt: Instant,
    val userId: Long,
    val userAgent: String? = null
)