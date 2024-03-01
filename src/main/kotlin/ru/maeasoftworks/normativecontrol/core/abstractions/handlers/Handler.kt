package ru.maeasoftworks.normativecontrol.core.abstractions.handlers

import ru.maeasoftworks.normativecontrol.core.abstractions.states.State
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext

/**
 * Parent class for any object handler that verifies and renders docx4j objects.
 * For correct registration use with [EagerInitialization] annotation.
 *
 * @sample ru.maeasoftworks.normativecontrol.core.implementations.predefined.JAXBElementHandler
 * @constructor Registers extending class to mapper container.
 * @param handlerConfig handler configuration created by [HandlerConfig.create].
 * @param T type of object that will be handled by this handler.
 * @param S type of [State] of handler. Pass [Nothing] if handler don't need it.
 */
abstract class Handler<T, S : State>(private val handlerConfig: HandlerConfig<T, S>) {
    init {
        HandlerMapper.map(handlerConfig)
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

    context(VerificationContext)
    @Suppress("UNCHECKED_CAST")
    val state: S
        get() {
            val key = handlerConfig.stateKey ?: throw UnsupportedOperationException("This object does not define any State")
            return states[key] as? S
                ?: handlerConfig.state?.invoke()?.also { states[key] = it }
                ?: throw UnsupportedOperationException("This object does not define any State")
        }
}