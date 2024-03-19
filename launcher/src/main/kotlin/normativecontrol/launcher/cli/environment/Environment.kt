package normativecontrol.launcher.cli.environment

import org.apache.commons.cli.MissingOptionException
import kotlin.reflect.KProperty

object Environment {
    fun variable(path: String) = EnvironmentVariable(path)

    fun optionalVariable(path: String) = EnvironmentVariableNullable(path)
}

val environment: Environment
    get() = Environment

interface AbstractEnvironmentVariable<T> {
    operator fun getValue(obj: Any, property: KProperty<*>): T
}

class EnvironmentVariable(private val path: String) : AbstractEnvironmentVariable<String> {
    private var value: String? = null

    override operator fun getValue(obj: Any, property: KProperty<*>): String {
        if (value == null) {
            value = System.getenv()[path] ?: throw MissingOptionException("Required environment variable $path not set.")
        }
        return value!!
    }
}

class EnvironmentVariableNullable(private val path: String) : AbstractEnvironmentVariable<String?> {
    private var value: String? = null
    private var initialized = false

    override operator fun getValue(obj: Any, property: KProperty<*>): String? {
        if (!initialized) {
            value = System.getenv()[path]
            initialized = true
        }
        return value
    }
}