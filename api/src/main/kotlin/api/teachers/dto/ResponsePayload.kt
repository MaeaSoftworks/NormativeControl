package api.teachers.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResponsePayload(
    val id: String,
    val status: Status
)