package normativecontrol.launcher

import org.apache.commons.cli.CommandLine

fun CommandLine.hasOption(arg: BootOptions): Boolean = hasOption(arg.option)

fun CommandLine.getOptionValue(arg: BootOptions): String? = getOptionValue(arg.option)