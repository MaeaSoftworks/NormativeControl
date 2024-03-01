package ru.maeasoftworks.normativecontrol.core.abstractions

import kotlin.collections.ArrayDeque
import kotlin.math.max

class Pointer {
    private val values = ArrayDeque<Int>()

    val value: List<Int>
        get() = values

    val size: Int
        get() = values.size

    val depth: Int
        get() = values.size - 1

    override fun toString(): String {
        return value.toString()
    }

    operator fun get(level: Int): Int {
        return values[level]
    }

    operator fun set(level: Int, value: Int) {
        if (values.size <= level) {
            values.addAll(sequence { repeat(level - (values.size - 1)) { yield(0) } })
        }
        values[level] = value
    }

    operator fun compareTo(other: Pointer): Int {
        for (i in 0..<max(size, other.size)) {
            val a = values.getOrNull(i)
            val b = other.values.getOrNull(i)
            if (a == null && b == null) {
                return 0
            }
            a ?: return -1 // more levels in b -> b more than a
            b ?: return 1 // more levels in a -> a more than b

            if (a < b) {
                return -1
            }
            if (a > b) {
                return 1
            }
        }
        return 0
    }

    fun last(): Int {
        return values.last()
    }

    fun clearTo(count: Int) {
        pop(values.size - count)
    }

    fun pop(count: Int) {
        values.size - count
        for (i in values.size downTo values.size - count + 1) {
            values.removeAt(i - 1)
        }
    }
}