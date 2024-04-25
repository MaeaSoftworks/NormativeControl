package normativecontrol.core.handlers

import normativecontrol.core.Configuration
import normativecontrol.core.Core
import normativecontrol.core.Runtime
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.utils.Event
import normativecontrol.shared.debug
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * Parent interface for any handler that verifies and renders docx4j objects.
 * For registration in [HandlerMapper] inheritor should have companion object
 * of type [Factory] and be annotated by [HandlerFactory] annotation.
 * @param T type of element that can be handled by this handler.
 */
abstract class Handler<T> {
    private val beforeHandleEvent = Event<Handler<T>, T>()
    private val afterHandleEvent = Event<Handler<T>, T>()
    @PublishedApi
    internal lateinit var runtime: Runtime

    open fun addHooks() {  }

    /**
     * External entrypoint to element handling.
     * Always will call [handle] with element cast to [T].
     * @param element an element that need to be handled
     */
    context(VerificationContext)
    fun handleElement(element: Any) {
        beforeHandleEvent(this)
        @Suppress("UNCHECKED_CAST")
        handle(element as T)
        afterHandleEvent(this)
    }

    /**
     * Handles the given element: processes verification and rendering.
     *
     * @param element an element that need to be handled
     */
    context(VerificationContext)
    protected abstract fun handle(element: T)

    protected inline fun add(hookInit: context(HookBuilder) () -> Unit) {
        hookInit(HookBuilder(runtime))
    }

    enum class HookType(@PublishedApi internal val event: Handler<*>.() -> Event<*, *>) {
        BeforeHandle({ beforeHandleEvent }),
        AfterHandle({ afterHandleEvent })
    }

    companion object {
        val logger = LoggerFactory.getLogger(Handler::class.java)
    }

    class HookBuilder(val runtime: Runtime) {
        inline fun <reified V, H: Handler<V>> hook(handler: KClass<H>, hookType: HookType, noinline hook: H.() -> Unit) {
            val instance = runtime.handlers[V::class] ?: throw NoSuchElementException("Handler not found")
            val event = hookType.event(instance) as Event<H, V>
            event.add(hook)
            logger.debug { "${this::class.qualifiedName} add hook of type ${hookType.name} to ${handler.qualifiedName}" }
        }
    }
}