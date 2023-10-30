package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
data class UsernameRequest(
    @field:Size(min = 4, max = 20)
    val username: String
)
