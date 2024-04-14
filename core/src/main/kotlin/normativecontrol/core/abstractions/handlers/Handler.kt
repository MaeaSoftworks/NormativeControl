package normativecontrol.core.abstractions.handlers

import normativecontrol.core.contexts.VerificationContext

abstract class Handler<T> {

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
    protected abstract fun handle(element: T)
}