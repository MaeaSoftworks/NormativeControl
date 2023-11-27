package ru.maeasoftworks.normativecontrol.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccessTokenResponse(
    val jwtToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
)
