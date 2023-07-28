package com.maeasoftworks.server.receivers

import com.maeasoftworks.server.services.ParserLauncher
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Configuration

@Configuration
@RabbitListener(queues = ["uploaded"])
class MessageReceiver(private val parserLauncher: ParserLauncher) {
    @RabbitHandler
    fun receive(message: String) {
        parserLauncher.run(message)
    }
}