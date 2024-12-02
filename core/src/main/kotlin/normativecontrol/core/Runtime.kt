package normativecontrol.core

import normativecontrol.core.annotations.CoreInternal
import normativecontrol.core.configurations.AbstractConfiguration
import normativecontrol.core.configurations.AbstractHandlerCollection
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.ExtendedHandler
import normativecontrol.core.handlers.HandlerPriority
import kotlin.reflect.KClass

/**
 * Document verification state holder. Allows to access state whenever in the code.
 * @param configurationName verification configuration name
 * @param collectionFactories factories of handler collections created by autoscan in [Core]
 */
class Runtime(
    configurationName: String,
    collectionFactories: Map<String, () -> AbstractHandlerCollection>
) {
    /**
     * Instance of current configuration.
     */
    val configuration = (collectionFactories[configurationName] ?: throw Exception("Collection with name $configurationName not found"))
        .invoke() as? AbstractConfiguration<*> ?: throw Exception("Collection with name $configurationName is not an Configuration")

    /**
     * Handler maps storage.
     */
    val handlers = HandlersHolder()

    /**
     * Current verification context.
     */
    lateinit var context: VerificationContext

    private val predefinedFactoriesMutable = predefinedFactories.toMutableMap()

    init {
        @OptIn(CoreInternal::class)
        run {
            factories[configurationName]?.forEach { (clazz, factory) ->
                var instance: AbstractHandler<*>? = null
                if (predefinedFactoriesMutable.containsKey(clazz)) {
                    val pre = predefinedFactoriesMutable[clazz]!!
                    if (pre.first == HandlerPriority.EXTENDABLE) {
                        predefinedFactoriesMutable.remove(clazz)
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

            predefinedFactoriesMutable.forEach { (clazz, pair) ->
                val (_, factory) = pair
                val instance = factory()
                instance.runtime = this
                handlers.handlersToOwnType[instance::class] = instance
                handlers.handlersToHandledType[clazz] = instance
            }

            handlers.handlersToHandledType.forEach { (_, handler) ->
                handler.subscribeToEvents()
            }
        }
    }

    /**
     * Handler maps storage.
     */
    @OptIn(CoreInternal::class)
    inner class HandlersHolder {
        @CoreInternal
        val handlersToHandledType: MutableMap<KClass<*>, AbstractHandler<*>> = mutableMapOf()

        @CoreInternal
        val handlersToOwnType: MutableMap<KClass<out AbstractHandler<*>>, AbstractHandler<*>> = mutableMapOf()

        /**
         * Get handler by handler's verified object type.
         * @param T type of verified object
         * @param elementType instance of the type of verified object
         * @return handler or null if handler with provided type was not found
         */
        operator fun <T : Any> get(elementType: T): AbstractHandler<T>? {
            @Suppress("UNCHECKED_CAST")
            return handlersToHandledType[elementType::class] as? AbstractHandler<T>
        }

        /**
         * Get handler by handler's KClass.
         * @param T type of handler
         * @param elementType KClass of handler
         * @return handler or null if handler of provided type was not found
         */
        operator fun <T : Any> get(elementType: KClass<out AbstractHandler<T>>): AbstractHandler<T>? {
            @Suppress("UNCHECKED_CAST")
            return handlersToOwnType[elementType] as? AbstractHandler<T>
        }
    }

    companion object {
        /**
         * Factories of handlers.
         */
        val factories = mutableMapOf<String, MutableMap<KClass<*>, () -> AbstractHandler<*>>>()

        /**
         * Factories of predefined handlers.
         */
        val predefinedFactories = mutableMapOf<KClass<*>, Pair<HandlerPriority, () -> AbstractHandler<*>>>()
    }
}