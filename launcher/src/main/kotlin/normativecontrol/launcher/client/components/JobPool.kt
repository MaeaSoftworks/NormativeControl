package normativecontrol.launcher.client.components

import normativecontrol.launcher.ParallelMode
import normativecontrol.shared.info
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable

object JobPool : Closeable {
    @PublishedApi
    internal lateinit var runner: Runner
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun initialize(parallelMode: ParallelMode) {
        runner = when (parallelMode) {
            ParallelMode.SINGLE -> {
                logger.info { "Blocking runner is set up" }
                BlockingRunner()
            }

            ParallelMode.THREADS -> {
                logger.info { "Multithreading runner is set up" }
                ThreadPoolRunner()
            }
        }
        ApplicationFinalizer.add(this)
    }

    fun run(runnable: Runnable) {
        runner.run(runnable)
    }

    override fun close() {
        runner.close()
    }
}