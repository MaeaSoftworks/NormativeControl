package normativecontrol.launcher

import normativecontrol.launcher.cli.BootConfiguration
import normativecontrol.launcher.cli.BootMode
import normativecontrol.launcher.client.Client

fun main(args: Array<String>) {
    val configuration = BootConfiguration(args)
    when (configuration.bootMode) {
        is BootMode.Lambda -> Lambda(configuration.bootMode.configuration).run()
        is BootMode.Client -> Client(configuration.bootMode.isBlocking).run()
        is BootMode.Help -> configuration.printHelp("core [OPTION]...")
    }
}