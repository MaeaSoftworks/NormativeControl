package ru.maeasoftworks.normativecontrol.api.domain.dao

import org.komapper.annotation.*
import java.time.Instant

@OptIn(KomapperExperimentalAssociation::class)
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
    val userId: String,
    val userAgent: String? = null
)