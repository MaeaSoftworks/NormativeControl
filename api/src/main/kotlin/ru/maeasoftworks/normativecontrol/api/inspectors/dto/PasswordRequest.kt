package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class PasswordRequest(
    @field:Size(min = 8, max = 120)
    val password: String
)
