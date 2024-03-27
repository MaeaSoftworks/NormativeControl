package normativecontrol.launcher.client.components

import normativecontrol.launcher.client.JobRunnable
import normativecontrol.launcher.client.messages.Job
import java.io.Closeable
import java.util.concurrent.Executors

object JobPool: Closeable {
    private val threads: Int = Runtime.getRuntime().availableProcessors()
    private val executor = Executors.newFixedThreadPool(threads)

    init {
        ApplicationFinalizer.add(this)
    }

    fun run(job: Job) {
        executor.submit(JobRunnable(job))
    }

    override fun close() {
        executor.shutdown()
        executor.close()
    }
}