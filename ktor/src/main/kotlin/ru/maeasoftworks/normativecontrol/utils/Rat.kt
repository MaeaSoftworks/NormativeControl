package ru.maeasoftworks.normativecontrol.utils

/**
 * Utility class to spy for a value in another coroutine without locking.
 *
 * @param T1 the type of the target object.
 * @param T2 the type of the report object.
 * @property path the path function that calculates the report based on the target object.
 * @property target the target object.
 */
class Rat<T1, T2>(private val path: (T1) -> T2) {
    internal var target: T1? = null

    /**
     * Generates a report based on the provided target.
     *
     * @return The generated report of type T2, or null if the target is null.
     */
    fun report(): T2? {
        return target?.let { path(it) }
    }
}

/**
 * Applies the given `Rat` instance to the current object.
 *
 * @param rat The `Rat` instance to apply.
 * @return The current object after applying the `Rat` instance.
 */
infix fun <T1, T2> T1.with(rat: Rat<T1, T2>): T1 {
    rat.target = this
    return this
}