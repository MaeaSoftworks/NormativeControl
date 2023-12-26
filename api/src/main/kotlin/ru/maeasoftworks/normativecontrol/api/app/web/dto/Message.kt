package ru.maeasoftworks.normativecontrol.api.app.web.dto

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val code: Code,
    val stage: Stage? = null,
    val message: String
) {
    constructor(id: String, code: Code, message: String) : this(id, code, null, message)

    enum class Code(code: Int) {
        INFO(10),
        WARN(20),
        ERROR(30),
        SUCCESS(50)
    }

    enum class Stage {
        INITIALIZATION,
        VERIFICATION,
        RENDERING,
        SAVING
    }
}