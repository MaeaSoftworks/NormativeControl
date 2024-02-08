package ru.maeasoftworks.normativecontrol.core.rendering

import java.io.Serializable

class LazySerializable(private val target: () -> String): Serializable {
    override fun toString(): String {
        return target()
    }
}

fun lazy(target: () -> String): LazySerializable {
    return LazySerializable(target)
}