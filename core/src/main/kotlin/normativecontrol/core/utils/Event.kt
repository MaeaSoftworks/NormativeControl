package normativecontrol.core.utils

open class Event<I> {
    private val callbacks = mutableSetOf<(I) -> Unit>()

    fun subscribe(callback: (I) -> Unit) {
        callbacks += callback
    }

    operator fun plusAssign(callback: (I) -> Unit) = subscribe(callback)

    operator fun invoke(arg: I) {
        callbacks.forEach {
            it(arg)
        }
    }
}