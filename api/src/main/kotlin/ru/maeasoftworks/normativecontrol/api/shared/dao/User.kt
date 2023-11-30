package ru.maeasoftworks.normativecontrol.api.shared.dao

import org.komapper.annotation.*

@KomapperEntity(["users"])
@KomapperTable("users")
data class User(
    @KomapperId
    @KomapperAutoIncrement
    @KomapperColumn("user_id")
    val id: Long = 0,
    var username: String,
    var password: String
)