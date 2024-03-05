package normativecontrol.launcher

import org.apache.commons.cli.*
import normativecontrol.launcher.cli.BootOptions
import normativecontrol.launcher.cli.OptionsComposer
import normativecontrol.launcher.cli.hasOption
import normativecontrol.launcher.client.Client
import normativecontrol.launcher.verifier.Verifier

fun main(args: Array<String>) {
    val options = OptionsComposer.composeOptions()
    val cli = DefaultParser().parse(options, args)
    if (cli.hasOption(BootOptions.Help)) {
        HelpFormatter().printHelp("core [OPTION]...", options)
        return
    }

    if (cli.hasOption(BootOptions.Verifier)) {
        Verifier(cli)
        return
    }

    if (cli.hasOption(BootOptions.ClientMode)) {
        Client()
    }
}