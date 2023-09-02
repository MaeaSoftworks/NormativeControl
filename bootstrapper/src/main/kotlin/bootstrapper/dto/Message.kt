package bootstrapper.dto

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val code: MessageCode,
    val message: String
)