package normativecontrol.launcher.client.components

import normativecontrol.launcher.client.messages.Job
import normativecontrol.shared.info
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Closeable

class JobPool(isBlocking: Boolean) : Closeable {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val runner: Runner

    init {
        if (_instance != null) throw UnsupportedOperationException("JobPool is already created")
        _instance = this
        runner = if (isBlocking) {
            logger.info { "Blocking runner is set up" }
            BlockingRunner()
        } else {
            logger.info { "Multithreading runner is set up" }
            ThreadPoolRunner()
        }
    }

    init {
        ApplicationFinalizer.add(this)
    }

    fun run(job: Job) {
        runner.run(job)
    }

    override fun close() {
        runner.close()
    }

    companion object {
        private var _instance: JobPool? = null

        val instance: JobPool
            get() = _instance ?: throw UnsupportedOperationException("JobPool is not initialized")

        fun run(job: Job) = instance.run(job)
    }
}