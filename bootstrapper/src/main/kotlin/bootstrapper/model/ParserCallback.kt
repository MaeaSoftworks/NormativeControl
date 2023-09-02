package bootstrapper.model

import bootstrapper.MessageSender
import bootstrapper.dto.Message
import bootstrapper.dto.MessageCode

class ParserCallback(
    private val documentId: String,
    private val messageSender: MessageSender
) {
    fun write(messageCode: MessageCode, message: String) {
        messageSender.send(Message(documentId, messageCode, message))
    }
}