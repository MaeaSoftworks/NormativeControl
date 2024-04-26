package normativecontrol.core.handlers

import normativecontrol.core.Runtime
import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.utils.Event

/**
 * Parent interface for any handler that verifies and renders docx4j objects.
 * For registration in [Runtime] inheritor should have companion object
 * of type [Factory] and be annotated by [Handler] annotation.
 * @param T type of element that can be handled by this handler.
 */
abstract class AbstractHandler<T> {
    private val beforeHandleEvent = Event<AbstractHandler<T>, T>()
    private val afterHandleEvent = Event<AbstractHandler<T>, T>()
    lateinit var runtime: Runtime
        internal set

    val ctx: VerificationContext
        get() = runtime.context

    open fun addHooks() {  }

    /**
     * External entrypoint to element handling.
     * Always will call [handle] with element cast to [T].
     * @param element an element that need to be handled
     */
    fun handleElement(element: Any) {
        beforeHandleEvent(this, ctx)
        with(ctx) {
            @Suppress("UNCHECKED_CAST")
            handle(element as T)
        }
        afterHandleEvent(this, ctx)
    }

    /**
     * Handles the given element: processes verification and rendering.
     *
     * @param element an element that need to be handled
     */
    context(VerificationContext)
    protected abstract fun handle(element: T)

    @Suppress("UNCHECKED_CAST")
    protected inline fun <H: AbstractHandler<V>, reified V> hook(hookType: HookType, noinline hook: H.(ctx: VerificationContext) -> Unit) {
        val instance = runtime.handlers[V::class] ?: return
        val event = hookType.event(instance) as? Event<H, V> ?: return
        event.add(hook)
    }

    enum class HookType(val event: AbstractHandler<*>.() -> Event<*, *>) {
        BeforeHandle({ beforeHandleEvent }),
        AfterHandle({ afterHandleEvent })
    }
}