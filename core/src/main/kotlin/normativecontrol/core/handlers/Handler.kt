package normativecontrol.core.handlers

import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.contexts.VerificationContext

/**
 * Parent interface for any handler that verifies and renders docx4j objects.
 * For registration in [HandlerMapper] inheritor should have companion object
 * of type [Factory] and be annotated by [HandlerFactory] annotation.
 * @param T type of element that can be handled by this handler.
 */
abstract class Handler<T> {
    /**
     * External entrypoint to element handling.
     * Always will call [handle] with element cast to [T].
     * @param element an element that need to be handled
     */
    context(VerificationContext)
    fun handleElement(element: Any) {
        @Suppress("UNCHECKED_CAST")
        handle(element as T)
    }

    /**
     * Handles the given element: processes verification and rendering.
     *
     * @param element an element that need to be handled
     */
    context(VerificationContext)
    protected abstract fun handle(element: T)
}