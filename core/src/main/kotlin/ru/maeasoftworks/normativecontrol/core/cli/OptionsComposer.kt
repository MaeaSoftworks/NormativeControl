package ru.maeasoftworks.normativecontrol.core.cli

import org.apache.commons.cli.Option
import org.apache.commons.cli.Options

object OptionsComposer {
    fun composeOptions(): Options {
        return Options().apply {
            BootOptions.entries.forEach { option ->
                addOption(
                    Option.builder(option.option)
                        .desc(option.description)
                        .hasArg(option.hasArg)
                        .apply { if (option.hasArg) argName(option.argName) }
                        .build()
                )
            }
        }
    }
}