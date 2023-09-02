package bootstrapper.configurations

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfiguration {
    @Bean
    fun uploadedQueue(): Queue {
        return Queue("uploaded")
    }

    @Bean
    fun resultsQueue(): Queue {
        return Queue("results")
    }
}