package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccessTokenResponse(
    val jwtToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
)
