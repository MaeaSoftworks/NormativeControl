package api.students.components

import api.students.dto.Message
import reactor.core.publisher.Sinks

class MessageSender(
    private val sink: Sinks.Many<Message>
) {
    fun send(body: Message) {
        sink.tryEmitNext(body)
    }
}