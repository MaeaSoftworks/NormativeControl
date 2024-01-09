package ru.maeasoftworks.normativecontrol.api.domain.dao

import org.komapper.annotation.*
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role

@Suppress("ArrayInDataClass")
@KomapperEntity(["users"])
@KomapperTable("users")
@KomapperOneToMany(targetEntity = Document::class, navigator = "documents")
data class User internal constructor(
    @KomapperId
    val id: String = null!!,
    var email: String,
    var password: String,
    @KomapperColumn("roles")
    var rolesStrings: Array<String> = emptyArray(),
    var isCredentialsVerified: Boolean = false
) {
    val roles: Array<Role>
        get() = rolesStrings.map { Role.valueOf(it) }.toTypedArray()

    constructor(
        id: String = null!!,
        email: String,
        password: String,
        roles: Array<Role> = emptyArray(),
        isCredentialsVerified: Boolean = false
    ) : this(id, email, password, roles.map { it.name }.toTypedArray(), isCredentialsVerified)
}