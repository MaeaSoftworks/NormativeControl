package api.teachers.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccessTokenResponse(
    val accessToken: String,
    val tokenType: String = "Bearer"
)
