package ru.maeasoftworks.normativecontrol.api.students.components

import ru.maeasoftworks.normativecontrol.api.students.dto.Message
import reactor.core.publisher.Sinks

class MessageSender(
    private val sink: Sinks.Many<Message>
) {
    fun send(body: Message) {
        sink.tryEmitNext(body)
    }
}