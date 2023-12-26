package ru.maeasoftworks.normativecontrol.api.app.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class PasswordRequest(
    val password: String
)