package normativecontrol.core.abstractions.handlers

interface Factory<T> {
    fun create(): T
}