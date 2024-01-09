package ru.maeasoftworks.normativecontrol.api.app.web.dto

import kotlinx.serialization.Serializable
import ru.maeasoftworks.normativecontrol.api.infrastructure.security.Role

@Suppress("ArrayInDataClass")
@Serializable
data class CredentialsResponse(
    val accessToken: String,

    val refreshToken: RefreshTokenResponse,

    val isCredentialsVerified: Boolean? = null,

    var roles: Array<Role> = emptyArray(),

    val tokenType: String = "Bearer"
)