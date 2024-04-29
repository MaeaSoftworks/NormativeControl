package normativecontrol.core.handlers

import normativecontrol.core.Runtime
import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.utils.Event

/**
 * Parent interface for any handler that verifies and renders docx4j objects.
 * For registration in [Runtime] inheritor should be annotated by [Handler] annotation.
 * @param T type of element that can be handled by this handler.
 */
abstract class AbstractHandler<T> {
    lateinit var runtime: Runtime
        internal set

    val hooks = Hooks()

    val ctx: VerificationContext
        get() = runtime.context

    open fun addHooks() {}

    /**
     * External entrypoint to element handling.
     * Always will call [handle] with element cast to [T].
     * @param element an element that need to be handled
     */
    fun handleElement(element: Any) {
        @Suppress("UNCHECKED_CAST")
        element as T

        with(ctx) {
            hooks.beforeHandle(element)
            handle(element)
            hooks.afterHandle(element)
        }
    }

    /**
     * Handles the given element: processes verification and rendering.
     *
     * @param element an element that need to be handled
     */
    context(VerificationContext)
    protected abstract fun handle(element: T)

    inline fun <reified H : AbstractHandler<*>> getHandlerOfType(): H? {
        return runtime.handlersToOwnType[H::class] as? H
    }

    inner class Hooks {
        val beforeHandle = Event<T>()
        val afterHandle = Event<T>()
    }
}