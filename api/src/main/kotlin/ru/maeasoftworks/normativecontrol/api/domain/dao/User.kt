package ru.maeasoftworks.normativecontrol.api.domain.dao

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.komapper.annotation.*
import ru.maeasoftworks.normativecontrol.api.domain.Organization
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role

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
    var serializedRoles: String,
    var isCredentialsVerified: Boolean = false
) {
    @setparam:KomapperIgnore
    var roles: Array<Role>
        get() = Json.decodeFromString(serializedRoles)
        set(value) { serializedRoles = Json.encodeToString(value) }

    constructor(
        id: String = null!!,
        email: String,
        organization: Organization,
        password: String,
        roles: Array<Role> = emptyArray(),
        isCredentialsVerified: Boolean = false
    ) : this(id, email, organization, password, Json.encodeToString(roles), isCredentialsVerified)
}