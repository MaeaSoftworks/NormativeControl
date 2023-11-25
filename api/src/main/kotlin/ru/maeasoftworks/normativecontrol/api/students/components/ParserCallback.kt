package ru.maeasoftworks.normativecontrol.api.students.components

import kotlinx.coroutines.runBlocking
import ru.maeasoftworks.normativecontrol.api.students.dto.Message

class ParserCallback(
    private val documentId: String,
    private val messageSender: MessageSender
) {
    fun write(messageCode: Message.Code, message: String) = runBlocking {
        messageSender.send(Message(documentId, messageCode, message))
    }
}
