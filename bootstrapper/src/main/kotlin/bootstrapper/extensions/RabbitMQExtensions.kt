package bootstrapper.extensions

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate

inline fun <reified T> RabbitTemplate.convertAndSend(queue: Queue, body: @Serializable T) {
    this.convertAndSend(queue.name, Json.encodeToString(body))
}