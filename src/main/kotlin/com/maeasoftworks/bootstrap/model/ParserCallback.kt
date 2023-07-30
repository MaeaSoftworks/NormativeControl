package com.maeasoftworks.bootstrap.model

import com.maeasoftworks.bootstrap.MessageSender
import com.maeasoftworks.bootstrap.dto.Message
import com.maeasoftworks.bootstrap.dto.MessageCode

class ParserCallback(
    private val documentId: String,
    private val messageSender: MessageSender
) {
    fun write(messageCode: MessageCode, message: String) {
        messageSender.send(Message(documentId, messageCode, message))
    }
}