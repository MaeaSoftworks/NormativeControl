package ru.maeasoftworks.normativecontrol.core.abstractions

import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization

/**
 * Parent class for any object handler that verifies and renders docx4j objects.
 * For correct registration use with [EagerInitialization] annotation.
 *
 * @sample ru.maeasoftworks.normativecontrol.core.implementations.predefined.JAXBElementHandler
 * @constructor Registers extending class to mapper container.
 * @param fn registration body. Must contain a call to [HandlerMapper.map].
 * @param T type of object that will be handled by this handler.
 */
abstract class Handler<T>(profile: Profile, mapping: Mapping<T>) {
    init {
        HandlerMapper.map(profile, mapping)
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