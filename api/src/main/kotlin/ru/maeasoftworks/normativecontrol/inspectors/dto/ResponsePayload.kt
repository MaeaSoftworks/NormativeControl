package ru.maeasoftworks.normativecontrol.inspectors.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResponsePayload(
    val id: String,
    val status: Status
)