package normativecontrol.core.utils

open class Event<T> {
    private val callbacks = mutableMapOf<Long, (T) -> Unit>()
    private val oneTimeCallbacks = mutableSetOf<Long>()
    private var lastSubId: Long = 0

    fun subscribe(callback: (T) -> Unit): Long {
        val id = getSubscriberId()
        callbacks[id] = callback
        return id
    }

    fun subscribeOnce(callback: (T) -> Unit) {
        val id = getSubscriberId()
        oneTimeCallbacks += id
        callbacks[id] = callback
    }

    operator fun invoke(arg: T) {
        callbacks.forEach { (_, subscriber) ->
            subscriber(arg)
        }
        oneTimeCallbacks.forEach {
            unsubscribe(it)
        }
        oneTimeCallbacks.clear()
    }

    // todo: automatic unsub or closeable?
    fun unsubscribe(code: Long) {
        callbacks.remove(code)
    }

    private fun getSubscriberId(): Long {
        return lastSubId++
    }
}