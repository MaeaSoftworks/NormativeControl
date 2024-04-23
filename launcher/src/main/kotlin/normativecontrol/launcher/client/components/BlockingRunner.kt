package normativecontrol.launcher.client.components

import normativecontrol.launcher.client.JobRunnable
import normativecontrol.launcher.client.messages.Job

class BlockingRunner : Runner {
    override fun run(job: Job) {
        JobRunnable(job).run()
    }

    override fun close() {}
}