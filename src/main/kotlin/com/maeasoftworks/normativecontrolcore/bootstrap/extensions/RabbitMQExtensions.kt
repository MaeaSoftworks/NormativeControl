package com.maeasoftworks.normativecontrolcore.bootstrap.extensions

import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate

fun <T> RabbitTemplate.convertAndSend(queue: Queue, body: T) {
    this.convertAndSend(queue.name, JSON.writeValueAsString(body))
}