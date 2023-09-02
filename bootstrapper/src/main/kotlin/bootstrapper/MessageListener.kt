package bootstrapper

import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
@RabbitListener(queues = ["uploaded"])
class MessageListener(private val launcher: Launcher) {
    @RabbitHandler
    fun receive(message: String) {
        launcher.run(message)
    }
}