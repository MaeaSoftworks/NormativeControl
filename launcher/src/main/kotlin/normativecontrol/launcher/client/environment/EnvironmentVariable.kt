package normativecontrol.launcher.client.environment

import org.apache.commons.cli.MissingOptionException
import kotlin.reflect.KProperty

class EnvironmentVariable(private val path: String) {
    private var value: String? = null

    operator fun getValue(obj: Any, property: KProperty<*>): String {
        if (value == null) {
            value = System.getenv()[path] ?: throw MissingOptionException("Required environment variable $path not set.")
        }
        return value!!
    }
}