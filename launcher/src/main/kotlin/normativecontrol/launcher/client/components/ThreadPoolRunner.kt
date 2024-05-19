package normativecontrol.launcher.client.components

import normativecontrol.core.exceptions.CoreException
import normativecontrol.launcher.client.JobRunnable
import normativecontrol.launcher.client.entities.Status
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ThreadPoolRunner : Runner {
    private val threads: Int = Runtime.getRuntime().availableProcessors()
    private val executor = Executors.newFixedThreadPool(threads)

    override fun run(runnable: Runnable) {
        if (runnable is JobRunnable) {
            val future = executor.submit(runnable)
            try {
                future.get(1, TimeUnit.MINUTES)
            } catch (e: TimeoutException) {
                future.cancel(true)
                runnable.job.sendResult(Status.ERROR, CoreException.Timeout(runnable.job.locale).localizedMessage)
            }
        } else {
            executor.submit(runnable)
        }
    }

    override fun close() {
        executor.shutdown()
        executor.close()
    }
}