package ru.maeasoftworks.normativecontrol.shared.dao

import org.komapper.annotation.*

@KomapperEntity(["users"])
@KomapperTable("users")
data class User(
    @KomapperId
    @KomapperAutoIncrement
    @KomapperColumn("user_id")
    val id: Long = 0,
    val username: String,
    val password: String
)