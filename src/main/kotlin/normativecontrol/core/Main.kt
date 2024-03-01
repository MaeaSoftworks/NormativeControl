package normativecontrol.core

import kotlinx.serialization.json.Json
import normativecontrol.core.abstractions.schema.Schema
import org.apache.commons.cli.*
import normativecontrol.core.cli.BootOptions
import normativecontrol.core.cli.OptionsComposer
import normativecontrol.core.cli.getOptionValue
import normativecontrol.core.cli.hasOption
import normativecontrol.core.configurations.VerificationConfiguration
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

        val schemaPath = cli.getOptionValue(BootOptions.Schema) ?: throw MissingArgumentException("${BootOptions.Verifier} requires '${BootOptions.Schema}' option.")

        val schema = Json.decodeFromString<Schema>(File(schemaPath).readText())
        Document(schema).apply {
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