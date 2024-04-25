package normativecontrol.core.utils

import normativecontrol.core.handlers.Handler

class Event<H : Handler<T>, T> {
    private val callbacks = mutableSetOf<H.() -> Unit>()

    fun add(callback: H.() -> Unit) {
        callbacks += callback
    }

    operator fun invoke(handler: H) {
        callbacks.forEach {
            it(handler)
        }
    }
}