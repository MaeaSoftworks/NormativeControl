package normativecontrol.launcher.client

import normativecontrol.core.configurations.VerificationConfiguration
import org.slf4j.LoggerFactory

class Client {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun run() {
        logger.info("Starting normative control core client mode...")
        VerificationConfiguration.initialize {
            forceStyleInlining = false
        }
        S3              // init block call
        Amqp.listen()
    }
}