package normativecontrol.launcher

import normativecontrol.core.Document
import normativecontrol.core.configurations.VerificationConfiguration
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUProfile
import java.awt.Desktop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

class Lambda(private val configuration: Configuration) {
    fun run() {
        val file = File(configuration.source)

        VerificationConfiguration.initialize { forceStyleInlining = false }

        Document(VerificationContext(UrFUProfile)).apply {
            load(file.inputStream())
            runVerification()
            val stream = ByteArrayOutputStream()
            writeResult(stream)
            FileOutputStream(configuration.result ?: (file.parent + File.separator + "result.docx")).use {
                stream.writeTo(it)
            }
            if (configuration.render) {
                Files.createTempFile("render-", ".html").toFile().also {
                    it.writeText(this.ctx.render.getString())
                    Desktop.getDesktop().browse(it.toURI())
                }
            }
        }
    }

    class Configuration(
        var source: String,
        var result: String?,
        var render: Boolean
    )
}