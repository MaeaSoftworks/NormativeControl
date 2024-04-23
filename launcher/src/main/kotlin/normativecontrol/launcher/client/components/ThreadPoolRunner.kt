package normativecontrol.launcher.client.components

import normativecontrol.launcher.client.JobRunnable
import normativecontrol.launcher.client.messages.Job
import java.util.concurrent.Executors

class ThreadPoolRunner : Runner {
    private val threads: Int = Runtime.getRuntime().availableProcessors()
    private val executor = Executors.newFixedThreadPool(threads)

    override fun run(job: Job) {
        executor.submit(JobRunnable(job))
    }

    override fun close() {
        executor.shutdown()
        executor.close()
    }
}