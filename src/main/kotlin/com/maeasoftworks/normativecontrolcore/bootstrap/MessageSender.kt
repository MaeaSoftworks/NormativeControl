package com.maeasoftworks.normativecontrolcore.bootstrap

import com.maeasoftworks.normativecontrolcore.bootstrap.extensions.convertAndSend
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class MessageSender(
    private val template: RabbitTemplate,
    private val resultsQueue: Queue
) {
    fun <T> send(body: T & Any) {
        template.convertAndSend(resultsQueue, body)
    }
}