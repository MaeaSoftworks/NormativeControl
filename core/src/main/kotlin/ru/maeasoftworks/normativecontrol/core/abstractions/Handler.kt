package ru.maeasoftworks.normativecontrol.core.abstractions

abstract class Handler<T>(fn: HandlerMapper.() -> Unit) {
    init {
        fn(HandlerMapper)
    }

    /**
     * Handles the given element: verification & rendering.
     *
     * __Important:__ please, cast element to T inside body.
     *
     * @param element the element to verify
     */
    abstract suspend fun handle(element: Any)
}