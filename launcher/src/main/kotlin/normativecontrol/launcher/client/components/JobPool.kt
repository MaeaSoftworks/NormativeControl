package normativecontrol.launcher.client.components

import normativecontrol.launcher.cli.ParallelMode
import normativecontrol.shared.info
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable

class JobPool(parallelMode: ParallelMode) : Closeable {
    @PublishedApi
    internal val runner: Runner

    init {
        if (_instance != null) throw UnsupportedOperationException("JobPool is already created")
        _instance = this
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

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(JobPool::class.java)
        private var _instance: JobPool? = null

        private val instance: JobPool
            get() = _instance ?: throw UnsupportedOperationException("JobPool is not initialized")

        fun run(runnable: Runnable) = instance.run(runnable)
    }
}