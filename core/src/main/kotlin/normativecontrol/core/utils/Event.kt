package normativecontrol.core.utils

open class Event<I> {
    private val callbacks = mutableMapOf<Long, (I) -> Unit>()
    private val oneTimeCallbacks = mutableSetOf<Long>()
    private var lastSubId: Long = 0

    private fun getSubscriberId(): Long {
        return lastSubId++
    }

    fun subscribe(callback: (I) -> Unit): Long {
        val id = getSubscriberId()
        callbacks[id] = callback
        return id
    }

    fun subscribeOnce(callback: (I) -> Unit) {
        val id = getSubscriberId()
        oneTimeCallbacks += id
        callbacks[id] = callback
    }

    operator fun invoke(arg: I) {
        callbacks.forEach { (_, subscriber) ->
            subscriber(arg)
        }
        oneTimeCallbacks.forEach {
            unsubscribe(it)
        }
    }

    fun unsubscribe(code: Long) {
        callbacks.remove(code)
    }
}