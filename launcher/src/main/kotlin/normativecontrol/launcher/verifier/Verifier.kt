package normativecontrol.launcher.verifier

import normativecontrol.core.Document
import normativecontrol.core.configurations.VerificationConfiguration
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUProfile
import normativecontrol.launcher.cli.BootOptions
import normativecontrol.launcher.cli.getOptionValue
import normativecontrol.launcher.cli.hasOption
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.MissingArgumentException
import java.awt.Desktop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

object Verifier {
    operator fun invoke(cli: CommandLine) {
        val path = cli.getOptionValue(BootOptions.Source) ?: throw MissingArgumentException("${BootOptions.Verifier} requires '${BootOptions.Source}' option.")
        val file = File(path)

        VerificationConfiguration.initialize { forceStyleInlining = cli.hasOption(BootOptions.Inline) }

        Document(VerificationContext(UrFUProfile)).apply {
            load(file.inputStream())
            runVerification()
            val stream = ByteArrayOutputStream()
            writeResult(stream)
            FileOutputStream(cli.getOptionValue(BootOptions.Result) ?: (file.parent + File.separator + "result.docx")).use {
                stream.writeTo(it)
            }
            if (cli.hasOption(BootOptions.Render)) {
                Files.createTempFile("render-", ".html").toFile().also {
                    it.writeText(this.ctx.render.getString())
                    Desktop.getDesktop().browse(it.toURI())
                }
            }
        }
    }
}