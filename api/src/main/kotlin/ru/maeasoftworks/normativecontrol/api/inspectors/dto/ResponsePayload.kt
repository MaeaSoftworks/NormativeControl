package ru.maeasoftworks.normativecontrol.api.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResponsePayload(
    val id: String,
    val status: Status
)
