package ru.maeasoftworks.normativecontrol.api.domain.dao

import org.komapper.annotation.*
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role

@KomapperEntity(["users"])
@KomapperTable("users")
data class User(
    @KomapperId
    @KomapperAutoIncrement
    @KomapperColumn("user_id")
    val id: Long = 0,
    var email: String,
    var password: String,
    @KomapperEnum(EnumType.NAME)
    var role: Role,
    var isCredentialsVerified: Boolean = false
)