package normativecontrol.core.rendering.html

import java.io.Serializable

/**
 * Utility class for serializable object which value is not defined at initialization.
 * @param value function that should return a finished result when [toString] called
 */
@JvmInline
value class LazySerializable(private val value: () -> String) : Serializable {
    override fun toString(): String {
        return value()
    }
}