package normativecontrol.core.abstractions.handlers

import normativecontrol.core.abstractions.states.State
import normativecontrol.core.abstractions.states.StateFactory
import normativecontrol.core.annotations.StateFactoryBind
import normativecontrol.core.contexts.VerificationContext

/**
 * Parent class for any object handler that verifies and renders docx4j objects.
 * For correct registration use with [normativecontrol.core.annotations.Handler] annotation.
 *
 * @sample normativecontrol.core.implementations.predefined.JAXBElementHandler
 */
abstract class AbstractHandler {
    @StateFactoryBind
    private var stateKey: StateFactory? = null

    context(VerificationContext)
    open val state: State?
        get() = abstractState

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
    val abstractState: State?
        get() = states[stateKey] ?: stateKey?.createState()?.also { states[stateKey ?: return null] = it }
}