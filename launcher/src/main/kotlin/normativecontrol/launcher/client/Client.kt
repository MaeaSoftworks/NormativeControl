package normativecontrol.launcher.client

import normativecontrol.launcher.client.components.Amqp
import normativecontrol.launcher.client.components.Database
import normativecontrol.launcher.client.components.S3
import org.slf4j.LoggerFactory

class Client {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun run() {
        logger.info("Starting normative control core client mode...")
        S3              // init block call
        Database
        Amqp.listen()
    }
}