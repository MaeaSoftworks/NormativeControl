package com.maeasoftworks.normativecontrolcore.bootstrap.model

import com.maeasoftworks.normativecontrolcore.bootstrap.MessageSender
import com.maeasoftworks.normativecontrolcore.bootstrap.dto.Message
import com.maeasoftworks.normativecontrolcore.bootstrap.dto.MessageCode

class ParserCallback(
    private val documentId: String,
    private val messageSender: MessageSender
) {
    fun write(messageCode: MessageCode, message: String) {
        messageSender.send(Message(documentId, messageCode, message))
    }
}