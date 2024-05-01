package normativecontrol.core.utils

open class Event<I> {
    private val callbacks = mutableMapOf<Long, (I) -> Unit>()
    private var lastSubscriber: Long = 0

    fun subscribe(callback: (I) -> Unit): Long {
        callbacks[lastSubscriber] = callback
        return lastSubscriber++
    }

    fun subscribeOnce(callback: (I) -> Unit) {
        val code = lastSubscriber++
        callbacks[code] = {
            callback(it)
            unsubscribe(code)
        }
    }

    operator fun invoke(arg: I) {
        callbacks.forEach { (_, subscriber) ->
            subscriber(arg)
        }
    }

    fun unsubscribe(code: Long) {
        callbacks.remove(code)
    }
}