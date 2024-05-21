package normativecontrol.core

import normativecontrol.core.annotations.CoreInternal
import normativecontrol.core.configurations.AbstractConfiguration
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.configurations.AbstractHandlerCollection
import normativecontrol.core.handlers.ExtendedHandler
import normativecontrol.core.handlers.HandlerPriority
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

    private val predefinedFactoriesDynamic = predefinedFactories.toMutableMap()

    lateinit var context: VerificationContext

    init {
        @OptIn(CoreInternal::class)
        factories[configurationName]?.forEach { (clazz, factory) ->
            var instance: AbstractHandler<*>? = null
            if (predefinedFactoriesDynamic.containsKey(clazz)) {
                val pre = predefinedFactoriesDynamic[clazz]!!
                if (pre.first == HandlerPriority.EXTENDABLE) {
                    predefinedFactoriesDynamic.remove(clazz)
                    @Suppress("UNCHECKED_CAST")
                    val extender = (factory() as AbstractHandler<Any>).also { it.runtime = this }
                    @Suppress("UNCHECKED_CAST")
                    val extended = (pre.second() as AbstractHandler<Any>).also { it.runtime = this }
                    instance = ExtendedHandler(extender, extended).also { it.runtime = this }

                    handlers.handlersToOwnType[extender::class] = extender
                    handlers.handlersToOwnType[extended::class] = extended
                    handlers.handlersToHandledType[clazz] = instance
                }
            }
            if (instance == null) {
                instance = factory()
                instance.runtime = this
                handlers.handlersToOwnType[instance::class] = instance
                handlers.handlersToHandledType[clazz] = instance
            }
        }

        @OptIn(CoreInternal::class)
        predefinedFactoriesDynamic.forEach { (clazz, pair) ->
            val (_, factory) = pair
            val instance = factory()
            instance.runtime = this
            handlers.handlersToOwnType[instance::class] = instance
            handlers.handlersToHandledType[clazz] = instance
        }

        @OptIn(CoreInternal::class)
        handlers.handlersToHandledType.forEach { (_, handler) ->
            handler.subscribeToEvents()
        }
    }

    @OptIn(CoreInternal::class)
    inner class HandlersHolder {
        @CoreInternal
        val handlersToHandledType: MutableMap<KClass<*>, AbstractHandler<*>> = mutableMapOf()
        @CoreInternal
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
        val predefinedFactories = mutableMapOf<KClass<*>, Pair<HandlerPriority, () -> AbstractHandler<*>>>()
    }
}