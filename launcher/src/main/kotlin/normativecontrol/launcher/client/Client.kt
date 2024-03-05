package normativecontrol.launcher.client

import normativecontrol.core.configurations.VerificationConfiguration

object Client {
    operator fun invoke() {
        VerificationConfiguration.initialize {
            forceStyleInlining = false
        }
        S3.initialize()
        Amqp.initialize()
        Amqp.listen()
    }
}