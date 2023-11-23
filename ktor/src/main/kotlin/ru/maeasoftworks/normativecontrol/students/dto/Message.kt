package ru.maeasoftworks.normativecontrol.students.dto

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val code: Code,
    val stage: Stage,
    val message: String
) {
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