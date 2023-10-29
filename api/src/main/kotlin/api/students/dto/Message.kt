package api.students.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Message(
    @get:JsonProperty("id")
    val id: String,
    @get:JsonProperty("code")
    val code: MessageCode,
    @get:JsonProperty("message")
    val message: String
) {
    constructor() : this("-1", MessageCode.ERROR, "")
}