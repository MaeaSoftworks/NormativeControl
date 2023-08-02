package com.maeasoftworks.normativecontrolcore.bootstrap

import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
@RabbitListener(queues = ["uploaded"])
class MessageListener(private val bootstrapper: Bootstrapper) {
    @RabbitHandler
    fun receive(message: String) {
        bootstrapper.run(message)
    }
}