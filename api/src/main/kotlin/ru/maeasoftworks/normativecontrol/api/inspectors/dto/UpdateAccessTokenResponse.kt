package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccessTokenResponse(
    val accessToken: String,
    val tokenType: String = "Bearer"
)
