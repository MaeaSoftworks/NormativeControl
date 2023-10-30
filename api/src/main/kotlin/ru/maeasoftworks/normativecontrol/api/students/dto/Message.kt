package ru.maeasoftworks.normativecontrol.api.students.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Message(
    @get:JsonProperty("id")
    val id: String,
    @get:JsonProperty("code")
    val code: Code,
    @get:JsonProperty("message")
    val message: String
) {
    constructor() : this("-1", Code.ERROR, "")

    enum class Code(code: Int) {
        INFO(10),
        WARN(20),
        ERROR(30),
        SUCCESS(50)
    }
}