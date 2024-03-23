package normativecontrol.launcher.client

import java.io.Closeable
import java.util.concurrent.Executors

object JobPool: Closeable {
    private val threads: Int = Runtime.getRuntime().availableProcessors()
    private val executor = Executors.newFixedThreadPool(threads)

    init {
        ApplicationFinalizer.add(this)
    }

    fun run(jobData: JobData) {
        executor.submit(JobRunnable(jobData))
    }

    override fun close() {
        executor.shutdown()
        executor.close()
    }
}