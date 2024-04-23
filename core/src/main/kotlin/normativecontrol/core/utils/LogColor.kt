package normativecontrol.core.utils

enum class LogColor(val value: String) {
    ANSI_BLACK("\u001B[30m"),
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_WHITE("\u001B[37m");

    companion object {
        const val ANSI_RESET = "\u001B[0m"
    }
}

fun String.highlight(color: LogColor): String {
    return "${color.value}${this}${LogColor.ANSI_RESET}"
}