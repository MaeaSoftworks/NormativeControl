package normativecontrol.core.abstractions.handlers

import normativecontrol.core.contexts.VerificationContext

/**
 * Parent interface for any object handler that verifies and renders docx4j objects.
 * For registration in [normativecontrol.core.abstractions.handlers.HandlerMapper]
 * annotate implementation with [normativecontrol.core.annotations.ReflectHandler] annotation.
 */
interface Handler<T> {
    /**
     * External entrypoint to element handling. It usually doesn't need to be overridden.
     *
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
    fun handle(element: T)
}