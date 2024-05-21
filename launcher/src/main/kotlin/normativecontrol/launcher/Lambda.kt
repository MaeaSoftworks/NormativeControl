package normativecontrol.launcher

import normativecontrol.core.Core
import normativecontrol.core.locales.Locales
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.launcher.cli.ParallelMode
import normativecontrol.launcher.client.components.JobPool
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

class Lambda(private val configuration: Configuration) {
    fun run() {
        JobPool.initialize(configuration.parallelMode)
        JobPool.run(runnable = {
            val file = File(configuration.source)
            val result = Core.verify(file.inputStream(), UrFUConfiguration.NAME, Locales.RU)
            result.docx.writeTo(FileOutputStream(configuration.result ?: (file.parent + File.separator + "result.docx")))
            if (configuration.render) {
                Files.createTempFile("render-", ".html").toFile().also {
                    it.writeText(result.html)
                    Desktop.getDesktop().browse(it.toURI())
                }
            }
        })
    }

    class Configuration(
        var source: String,
        var result: String?,
        var render: Boolean,
        var parallelMode: ParallelMode
    )
}