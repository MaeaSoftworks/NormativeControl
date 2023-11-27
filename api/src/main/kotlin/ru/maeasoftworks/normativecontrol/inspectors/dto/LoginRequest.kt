package ru.maeasoftworks.normativecontrol.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)
