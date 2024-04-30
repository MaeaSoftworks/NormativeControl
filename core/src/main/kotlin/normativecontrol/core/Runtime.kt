package normativecontrol.core

import normativecontrol.core.configurations.AbstractConfiguration
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.configurations.AbstractHandlerCollection
import normativecontrol.core.predefined.Predefined
import kotlin.reflect.KClass

/**
 * Object that hold all state of file verification. Used for easy access
 * to contexts from any place in file verification.
 * @param configurationName name of current configuration
 * @param collectionFactories factories of handler collections created by autoscan in [Core]
 */
class Runtime(
    configurationName: String,
    collectionFactories: Map<String, () -> AbstractHandlerCollection>
) {
    val configuration = (collectionFactories[configurationName] ?: throw Exception("Collection with name $configurationName not found"))
        .invoke() as? AbstractConfiguration<*> ?: throw Exception("Collection with name $configurationName is not an Configuration")
    val handlers = HandlersHolder()

    lateinit var context: VerificationContext

    init {
        @OptIn(PrivateDirectAccess::class)
        factories[configurationName]?.forEach { (clazz, factory) ->
            val instance = factory()
            instance.runtime = this
            handlers.handlersToOwnType[instance::class] = instance
            handlers.handlersToHandledType[clazz] = instance
        }

        @OptIn(PrivateDirectAccess::class)
        factories[Predefined.NAME]?.forEach { (clazz, factory) ->
            val instance = factory()
            instance.runtime = this
            handlers.handlersToOwnType[instance::class] = instance
            handlers.handlersToHandledType[clazz] = instance
        }

        @OptIn(PrivateDirectAccess::class)
        handlers.handlersToHandledType.forEach { (_, handler) ->
            handler.addHooks()
        }
    }

    @OptIn(PrivateDirectAccess::class)
    inner class HandlersHolder {
        @PrivateDirectAccess
        val handlersToHandledType: MutableMap<KClass<*>, AbstractHandler<*>> = mutableMapOf()
        @PrivateDirectAccess
        val handlersToOwnType: MutableMap<KClass<out AbstractHandler<*>>, AbstractHandler<*>> = mutableMapOf()

        operator fun <T: Any> get(elementType: T): AbstractHandler<T>? {
            @Suppress("UNCHECKED_CAST")
            return handlersToHandledType[elementType::class] as? AbstractHandler<T>
        }

        operator fun <T: Any> get(elementType: KClass<out AbstractHandler<T>>): AbstractHandler<T>? {
            @Suppress("UNCHECKED_CAST")
            return handlersToOwnType[elementType] as? AbstractHandler<T>
        }
    }

    companion object {
        val factories = mutableMapOf<String, MutableMap<KClass<*>, () -> AbstractHandler<*>>>()
    }

    @RequiresOptIn(level = RequiresOptIn.Level.ERROR)
    private annotation class PrivateDirectAccess
}