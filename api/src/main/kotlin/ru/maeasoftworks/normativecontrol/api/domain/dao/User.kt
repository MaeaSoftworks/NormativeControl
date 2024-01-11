package ru.maeasoftworks.normativecontrol.api.domain.dao

import org.komapper.annotation.*
import ru.maeasoftworks.normativecontrol.api.domain.Organization
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role

@Suppress("ArrayInDataClass")
@KomapperEntity(["users"])
@KomapperTable("users")
@KomapperOneToMany(targetEntity = Document::class, navigator = "documents")
data class User internal constructor(
    @KomapperId
    val id: String = null!!,
    var email: String,
    @KomapperEnum(EnumType.NAME)
    var organization: Organization,
    var password: String,
    @KomapperColumn("roles")
    var rolesStrings: Array<String> = emptyArray(),
    var isCredentialsVerified: Boolean = false
) {
    @setparam:KomapperIgnore
    var roles: Array<Role>
        get() = rolesStrings.map { Role.valueOf(it) }.toTypedArray()
        set(value) { rolesStrings = value.map { it.name }.toTypedArray() }

    constructor(
        id: String = null!!,
        email: String,
        organization: Organization,
        password: String,
        roles: Array<Role> = emptyArray(),
        isCredentialsVerified: Boolean = false
    ) : this(id, email, organization, password, roles.map { it.name }.toTypedArray(), isCredentialsVerified)
}