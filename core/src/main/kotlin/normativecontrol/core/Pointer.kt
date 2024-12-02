package normativecontrol.core

import kotlin.math.max

/**
 * Pointer to the current verified element in document.
 */
class Pointer {
    private val values = ArrayDeque<Int>()

    /**
     * Pointer value.
     */
    val value: List<Int>
        get() = values

    /**
     * Pointer size (for cycles started from 0).
     */
    val size: Int
        get() = values.size

    /**
     * Pointer depth.
     */
    val depth: Int
        get() = values.size - 1

    override fun toString(): String {
        return value.toString()
    }

    /**
     * Get current pointer value on depth level.
     * @param level depth level
     * @return pointer value
     */
    operator fun get(level: Int): Int {
        return values[level]
    }

    /**
     * Set pointer value on selected depth level. If level is not less that pointer size
     * value will be prepended by zeros.
     * @param level depth level
     * @param value new value
     */
    @PointerTransformations
    operator fun set(level: Int, value: Int) {
        if (values.size <= level) {
            values.addAll(
                sequence {
                    repeat(level - (values.size - 1)) {
                        yield(0)
                    }
                }
            )
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

    /**
     * Pointer value on last depth level.
     * @return pointer level
     */
    fun last(): Int {
        return values.last()
    }

    /**
     * Reset pointer depth to [count].
     * @param count depth level which must be left
     */
    @PointerTransformations
    fun clearTo(count: Int) {
        pop(values.size - count)
    }

    /**
     * Drop pointer depth levels.
     * @param count depth level which must be dropped
     */
    @PointerTransformations
    fun pop(count: Int) {
        values.size - count
        for (i in values.size downTo values.size - count + 1) {
            values.removeAt(i - 1)
        }
    }

    @RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "This functions transform pointer's state and should not be called outside core module.")
    @Target(AnnotationTarget.FUNCTION)
    internal annotation class PointerTransformations
}