package normativecontrol.launcher.client

import normativecontrol.launcher.ParallelMode
import normativecontrol.launcher.client.components.Amqp
import normativecontrol.launcher.client.components.Database
import normativecontrol.launcher.client.components.JobPool
import normativecontrol.launcher.client.components.S3
import org.slf4j.LoggerFactory
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(name = "client", description = ["Start client server."], mixinStandardHelpOptions = true)
class Client : Runnable {
    @Option(names = ["-b"], description = ["Enable blocking mode (instead of multithreading)."])
    private var isBlocking = false

    private val parallelMode: ParallelMode
        get() = if (isBlocking) ParallelMode.SINGLE else ParallelMode.THREADS

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun run() {
        logger.info("Starting normative control core client mode...")
        JobPool.initialize(parallelMode)
        S3              // init block call
        Database
        Amqp.listen()
    }
}