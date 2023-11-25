package ru.maeasoftworks.normativecontrol.api.students.dto

data class Message(
    val id: String,
    val code: Code,
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
