package normativecontrol.launcher.cli.environment

import org.apache.commons.cli.MissingOptionException

object Environment {
    fun variable(path: String): String {
        return System.getenv()[path] ?: throw MissingOptionException("Required environment variable $path not set.")
    }

    inline fun <T> variable(path: String, converter: (String) -> T): T {
        return converter(System.getenv()[path] ?: throw MissingOptionException("Required environment variable $path not set."))
    }

    fun optionalVariable(path: String): String? {
        return System.getenv()[path]
    }

    inline fun <T> optionalVariable(path: String, converter: (String?) -> T?): T? {
        return converter(System.getenv()[path])
    }
}

inline val environment: Environment
    get() = Environment