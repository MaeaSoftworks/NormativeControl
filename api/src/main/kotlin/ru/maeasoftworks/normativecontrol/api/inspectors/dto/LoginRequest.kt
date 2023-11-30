package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)
