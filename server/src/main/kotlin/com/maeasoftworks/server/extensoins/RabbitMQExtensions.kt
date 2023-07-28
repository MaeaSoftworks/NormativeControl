package com.maeasoftworks.server.extensoins

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate

private val mapper = ObjectMapper()

fun <T> RabbitTemplate.convertAndSend(queue: Queue, body: T) {
    this.convertAndSend(queue.name, mapper.writeValueAsString(body))
}