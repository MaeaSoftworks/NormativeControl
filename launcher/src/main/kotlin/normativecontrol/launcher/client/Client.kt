package normativecontrol.launcher.client

import org.slf4j.LoggerFactory

class Client {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun run() {
        logger.info("Starting normative control core client mode...")
        S3              // init block call
        Amqp.listen()
    }
}