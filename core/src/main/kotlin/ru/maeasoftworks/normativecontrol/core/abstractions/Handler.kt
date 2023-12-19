package ru.maeasoftworks.normativecontrol.core.abstractions

import me.prmncr.hotloader.HotLoaded

/**
 * Parent class for any object handler that verifies and renders docx4j objects.
 * For correct registration use with [HotLoaded] annotation.
 *
 * @constructor Registers extending class to mapper container.
 * @param fn registration body. Must contain a call to [HandlerMapper.register].
 * @param T type of object that will be handled by this handler.
 */
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