package normativecontrol.launcher

import normativecontrol.core.Core
import normativecontrol.implementation.urfu.UrFUConfiguration
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

class Lambda(private val configuration: Configuration) {
    fun run() {
        val file = File(configuration.source)
        val result = Core.verify(file.inputStream(), UrFUConfiguration())
        result.docx.writeTo(FileOutputStream(configuration.result ?: (file.parent + File.separator + "result.docx")))
        if (configuration.render) {
            Files.createTempFile("render-", ".html").toFile().also {
                it.writeText(result.html)
                Desktop.getDesktop().browse(it.toURI())
            }
        }
    }

    class Configuration(
        var source: String,
        var result: String?,
        var render: Boolean
    )
}