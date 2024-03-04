package normativecontrol.core.utils

import java.io.Serializable

class LazySerializable(private val target: () -> String) : Serializable {
    override fun toString(): String {
        return target()
    }
}

fun lazySerializable(target: () -> String): LazySerializable {
    return LazySerializable(target)
}