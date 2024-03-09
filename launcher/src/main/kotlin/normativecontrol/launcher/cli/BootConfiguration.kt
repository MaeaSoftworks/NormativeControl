package normativecontrol.launcher.cli

import normativecontrol.launcher.Lambda
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.MissingOptionException
import org.apache.commons.cli.Options

class BootConfiguration(args: Array<String>) {
    private val options: Options = OptionsComposer.composeOptions()
    val bootMode: BootMode

    init {
        val cli = DefaultParser().parse(options, args)
        bootMode = if (cli.hasOption(BootOptions.Lambda.option)) {
            BootMode.Lambda(
                Lambda.Configuration(
                    cli.getOptionValue(BootOptions.Source.option)
                        ?: throw MissingOptionException("${BootOptions.Lambda} requires '${BootOptions.Source}' option."),
                    cli.getOptionValue(BootOptions.Result.option),
                    cli.hasOption(BootOptions.Render.option)
                )
            )
        } else if (cli.hasOption(BootOptions.Help.option)) {
            BootMode.Help
        } else {
            BootMode.Client
        }
    }

    fun printHelp(syntax: String) {
        HelpFormatter().printHelp(syntax, options)
    }
}