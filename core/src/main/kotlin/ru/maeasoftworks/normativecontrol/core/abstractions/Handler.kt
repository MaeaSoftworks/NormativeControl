package ru.maeasoftworks.normativecontrol.core.abstractions

import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

/**
 * Parent class for any object handler that verifies and renders docx4j objects.
 * For correct registration use with [EagerInitialization] annotation.
 *
 * @sample ru.maeasoftworks.normativecontrol.core.implementations.predefined.JAXBElementHandler
 * @constructor Registers extending class to mapper container.
 * @param profile verification profile.
 * @param mapping handler mapping created by [Mapping.of].
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
    context(VerificationContext)
    abstract fun handle(element: Any)
}