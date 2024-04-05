package normativecontrol.launcher.client.components

import normativecontrol.launcher.client.messages.Job
import java.io.Closeable

interface Runner : Closeable {
    fun run(job: Job)
}