package normativecontrol.core.handlers

import normativecontrol.core.Runtime
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.utils.Event

/**
 * Parent interface for any handler that verifies and renders docx4j objects.
 * For registration in [Runtime] inheritor should be annotated by [Handler] annotation.
 * @param T type of element that can be handled by this handler.
 */
abstract class AbstractHandler<T: Any> {
    lateinit var runtime: Runtime
        internal set

    /**
     * Easy access to [VerificationContext] where it is not defined.
     */
    val ctx: VerificationContext
        get() = runtime.context

    /**
     * Function in which hooks for other handlers
     * can be placed and initialized.
     *
     * **Note**: placing hooks in the `init` block probably
     * will return `null` instead of handler.
     */
    open fun subscribeToEvents() {}

    /**
     * External entrypoint to element handling.
     * Always will call [handle] with element cast to [T].
     * @param element an element that need to be handled
     */
    fun handleElement(element: Any) {
        @Suppress("UNCHECKED_CAST")
        element as T

        with(ctx) {
            events.beforeHandle(element)
            handle(element)
            events.afterHandle(element)
        }
    }

    /**
     * Handles the given element: processes verification and rendering.
     *
     * @param element an element that need to be handled
     */
    context(VerificationContext)
    protected abstract fun handle(element: T)

    /**
     * Handler's hooks. Every handler have its own hooks.
     */
    val events = Events()

    inner class Events {
        /**
         * Event that will be called before every [handle] function call with element as parameter.
         */
        val beforeHandle = Event<T>()

        /**
         * Event that will be called after every [handle] function call with element as parameter.
         */
        val afterHandle = Event<T>()
    }
}