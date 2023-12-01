package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class PasswordRequest(
    val password: String
)