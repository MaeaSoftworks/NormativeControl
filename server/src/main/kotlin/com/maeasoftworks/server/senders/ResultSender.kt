package com.maeasoftworks.server.senders

import com.maeasoftworks.server.extensoins.convertAndSend
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class ResultSender(
    private val template: RabbitTemplate,
    private val resultsQueue: Queue
) {
    fun <T> send(body: T & Any) {
        template.convertAndSend(resultsQueue, body)
    }
}