package normativecontrol.launcher.cli.environment

import org.apache.commons.cli.MissingOptionException

fun Environment.variable(path: String): String {
    return System.getenv()[path] ?: throw MissingOptionException("Required environment variable $path not set.")
}

inline fun <T> Environment.variable(path: String, converter: (String) -> T): T {
    return converter(System.getenv()[path] ?: throw MissingOptionException("Required environment variable $path not set."))
}

fun Environment.optionalVariable(path: String): String? {
    return System.getenv()[path]
}

inline fun <T> Environment.optionalVariable(path: String, converter: (String?) -> T?): T? {
    return converter(System.getenv()[path])
}