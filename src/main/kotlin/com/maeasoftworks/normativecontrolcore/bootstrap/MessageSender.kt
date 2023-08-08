package com.maeasoftworks.normativecontrolcore.bootstrap

import com.maeasoftworks.normativecontrolcore.bootstrap.extensions.convertAndSend
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class MessageSender(
    val template: RabbitTemplate,
    val resultsQueue: Queue
) {
    final inline fun <reified T> send(body: T & Any) {
        template.convertAndSend(resultsQueue, body)
    }
}