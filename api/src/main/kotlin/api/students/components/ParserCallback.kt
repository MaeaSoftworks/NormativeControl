package api.students.components

import api.students.dto.Message
import api.students.dto.MessageCode

class ParserCallback(
    private val documentId: String,
    private val messageSender: MessageSender
) {
    fun write(messageCode: MessageCode, message: String) {
        messageSender.send(Message(documentId, messageCode, message))
    }
}