package api.teachers.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val accessToken: String,

    val refreshToken: String,

    val tokenType: String = "Bearer"
)