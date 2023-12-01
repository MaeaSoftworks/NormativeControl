package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsernameRequest(
    val username: String
)