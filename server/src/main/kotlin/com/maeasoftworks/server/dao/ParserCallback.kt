package com.maeasoftworks.server.dao

import com.maeasoftworks.server.senders.ResultSender

class ParserCallback(
    private val documentId: String,
    private val resultSender: ResultSender
) {
    fun write(messageCode: MessageCode, message: String) {
        resultSender.send(Message(documentId, messageCode, message))
    }
}