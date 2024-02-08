package ru.maeasoftworks.normativecontrol.core.abstractions

import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

/**
 * Parent class for any object handler that verifies and renders docx4j objects.
 * For correct registration use with [EagerInitialization] annotation.
 *
 * @sample ru.maeasoftworks.normativecontrol.core.implementations.predefined.JAXBElementHandler
 * @constructor Registers extending class to mapper container.
 * @param config handler mapping created by [Config.create].
 * @param T type of object that will be handled by this handler.
 * @param S type of [State] of handler. Pass [Nothing] if handler don't need it.
 */
abstract class Handler<T, S: State>(private val config: Config<T, S>) {
    init {
        HandlerMapper.map(config)
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
        get() = (Proxy.localContexts[config.handler()]
            ?: config.state?.invoke()?.also { Proxy.localContexts[config.handler()] = it }) as? S
            ?: throw UnsupportedOperationException("This object does not define any LocalContext")

    private object Proxy: ProxyContext {
        context(VerificationContext)
        inline val localContexts: MutableMap<Handler<*, *>, State>
            get() = this@VerificationContext.localContexts
    }
}

