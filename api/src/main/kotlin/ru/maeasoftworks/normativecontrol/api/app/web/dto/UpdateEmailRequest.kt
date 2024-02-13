package ru.maeasoftworks.normativecontrol.api.app.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateEmailRequest(
    val email: String
)

@Serializable
data class UpdateEmailStudentRequest(
    val email: String
)