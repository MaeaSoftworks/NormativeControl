package normativecontrol.core.utils

abstract class Counter<T> {
    abstract var value: T
        protected set

    var isReset: Boolean = false
        protected set

    abstract fun increment(): Counter<T>

    abstract fun reset()
}

class IntCounter: Counter<Int>() {
    override var value: Int = -1

    override fun increment(): Counter<Int> {
        value++
        isReset = false
        return this
    }

    override fun reset() {
        value = 0
        isReset = true
    }

    override fun toString(): String {
        return value.toString()
    }
}