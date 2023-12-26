package ru.maeasoftworks.normativecontrol.api.app.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccessTokenResponse(
    val jwtToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
)