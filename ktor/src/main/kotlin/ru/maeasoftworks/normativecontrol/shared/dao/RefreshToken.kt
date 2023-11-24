package ru.maeasoftworks.normativecontrol.shared.dao

import org.komapper.annotation.*

@KomapperTable("refresh_tokens")
@KomapperEntity
@KomapperOneToOne(User::class)
data class RefreshToken(
    @KomapperId
    @KomapperAutoIncrement
    @KomapperColumn("user_id")
    val id: Long = 0,
    val refreshToken: String,
    val user: User
)