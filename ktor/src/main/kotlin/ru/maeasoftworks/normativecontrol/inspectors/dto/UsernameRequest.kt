package ru.maeasoftworks.normativecontrol.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsernameRequest(
    val username: String
)
