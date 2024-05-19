package normativecontrol.launcher.cli

import normativecontrol.launcher.Lambda
import normativecontrol.launcher.client.Client
import org.apache.commons.cli.*

class BootConfiguration(args: Array<String>) {
    private val options: Options = createOptions()

    val bootMode: BootMode

    init {
        val cli = DefaultParser().parse(options, args)
        val parallelMode = if (cli.hasOption(BootOptions.Blocking.option)) {
            ParallelMode.SINGLE
        } else {
            ParallelMode.THREADS
        }
        bootMode = if (cli.hasOption(BootOptions.Lambda.option)) {
            BootMode.Lambda(
                Lambda.Configuration(
                    cli.getOptionValue(BootOptions.Source.option)
                        ?: throw MissingOptionException("${BootOptions.Lambda} requires '${BootOptions.Source}' option."),
                    cli.getOptionValue(BootOptions.Result.option),
                    cli.hasOption(BootOptions.Render.option),
                    parallelMode
                )
            )
        } else if (cli.hasOption(BootOptions.Help.option)) {
            BootMode.Help
        } else {
            BootMode.Client(Client.Configuration(parallelMode))
        }
    }

    fun printHelp(syntax: String) {
        HelpFormatter().printHelp(syntax, options)
    }

    private fun createOptions(): Options {
        return Options().apply {
            BootOptions.entries
                .map {
                    Option.builder(it.option)
                        .desc(it.description)
                        .hasArg(it.hasArg)
                        .apply { if (it.hasArg) argName(it.argName) }
                        .build()
                }
                .forEach(::addOption)
        }
    }
}