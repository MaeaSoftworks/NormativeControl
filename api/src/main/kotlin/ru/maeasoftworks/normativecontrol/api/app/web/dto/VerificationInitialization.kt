package ru.maeasoftworks.normativecontrol.api.app.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerificationInitialization(
    val fingerprint: String?,
    val length: Int
)