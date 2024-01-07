package ru.maeasoftworks.normativecontrol.api.domain.dao

import org.komapper.annotation.*
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role

@KomapperEntity(["users"])
@KomapperTable("users")
@KomapperOneToMany(targetEntity = Document::class, navigator = "documents")
data class User(
    @KomapperId
    val id: String,
    var email: String,
    var password: String,
    @KomapperEnum(EnumType.NAME)
    var role: Role = Role.STUDENT,
    var isCredentialsVerified: Boolean = false
)