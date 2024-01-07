package ru.maeasoftworks.normativecontrol.api.domain.dao

import org.komapper.annotation.*
import java.time.Instant

@KomapperEntity(["verificationCodes"])
@KomapperTable("verification_codes")
@KomapperOneToOne(targetEntity = User::class)
data class VerificationCode(
    @KomapperId
    @KomapperAutoIncrement
    val id: Long = 0,
    val userId: String,
    val code: Int,
    val createdAt: Instant,
    val expiresAt: Instant
)