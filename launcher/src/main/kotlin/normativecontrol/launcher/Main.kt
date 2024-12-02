package normativecontrol.launcher

import picocli.CommandLine
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    exitProcess(CommandLine(NormativeControl()).execute(*args))
}