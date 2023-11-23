package ru.maeasoftworks.normativecontrol.dao

import org.komapper.annotation.*

@KomapperEntity
data class Users(
    @KomapperId
    @KomapperAutoIncrement
    @KomapperColumn("user_id")
    val id: Long = 0,
    val username: String,
    val password: String
)