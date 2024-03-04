package normativecontrol.launcher

import normativecontrol.core.Document
import org.apache.commons.cli.*
import normativecontrol.launcher.cli.BootOptions
import normativecontrol.launcher.cli.OptionsComposer
import normativecontrol.launcher.cli.getOptionValue
import normativecontrol.launcher.cli.hasOption
import normativecontrol.core.configurations.VerificationConfiguration
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUProfile
import java.awt.Desktop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files

fun main(args: Array<String>) {
    val options = OptionsComposer.composeOptions()
    val help = { HelpFormatter().printHelp("core [OPTION]...", options) }
    val cli = DefaultParser().parse(options, args)
    if (cli.hasOption(BootOptions.Help)) {
        help()
    }

    if (cli.hasOption(BootOptions.Verifier)) {
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