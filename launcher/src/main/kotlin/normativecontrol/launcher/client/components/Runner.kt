package normativecontrol.launcher.client.components

import java.io.Closeable

interface Runner : Closeable {
    fun run(runnable: Runnable)
}