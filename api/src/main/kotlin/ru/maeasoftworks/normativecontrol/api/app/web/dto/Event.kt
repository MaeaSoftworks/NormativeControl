package ru.maeasoftworks.normativecontrol.api.app.web.dto

data class Event(val event: String, val data: String) {
    override fun toString(): String {
        return "event: $event\ndata: $data\n\n"
    }

    companion object {
        fun fromMessage(message: Message) = message.asEvent({ code.name }, { this.toString() })
    }
}

inline fun <T> T.asEvent(event: T.() -> String, data: T.() -> String): Event {
    return Event(event(), data())
}