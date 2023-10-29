package api.teachers.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @field:NotBlank
    val username: String,

    @field:Valid
    @field:NotBlank
    val password: String
)
