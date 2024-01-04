package ru.maeasoftworks.normativecontrol.api.app.web.dto

sealed class Message(val code: Code, stage: Stage?, message: String?, vararg args: Pair<String, String?>) : java.io.Serializable {
    private val body = mutableMapOf<String, String?>()

    init {
        body["code"] = code.name
        body["stage"] = stage?.name
        body["message"] = message
        body.putAll(args)
    }

    override fun toString(): String {
        return "{${body.entries.joinToString("") { if (it.value != null) "\"${it.key}\":\"${it.value}\"," else "" }.removeSuffix(",") }}"
    }

    class Progress(value: Double, stage: Stage, vararg args: Pair<String, String?>):
        Message(Code.PROGRESS, stage, null, *args, "value" to value.toString())
    class Success(id: String, vararg args: Pair<String, String?>):
        Message(Code.SUCCESS, null, null, *args, "id" to id)
    class Info(message: String, vararg args: Pair<String, String?>):
        Message(Code.INFO, null, message = message, *args)
    class Warn(description: String, vararg args: Pair<String, String?>):
        Message(Code.WARN, null, null, *args, "description" to description)
    class Error(description: String, vararg args: Pair<String, String?>):
        Message(Code.ERROR, null, null, *args, "description" to description)

    enum class Code {
        INFO,
        PROGRESS,
        WARN,
        ERROR,
        SUCCESS
    }

    enum class Stage {
        INITIALIZATION,
        VERIFICATION,
        SAVING
    }
}