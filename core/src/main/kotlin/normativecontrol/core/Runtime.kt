package normativecontrol.core

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.HandlerCollection
import normativecontrol.core.predefined.Predefined
import kotlin.reflect.KClass

class Runtime(runtimeConfigurationName: String, collectionFactories: Map<String, () -> HandlerCollection>) {
    val configuration = (collectionFactories[runtimeConfigurationName] ?: throw Exception("Collection with name $runtimeConfigurationName not found"))
        .invoke() as? AbstractConfiguration<*> ?: throw Exception("Collection with name $runtimeConfigurationName is not an Configuration")

    private val predefined = (collectionFactories[Predefined.NAME] ?: throw Exception("Predefined collection was not found")).invoke()

    val handlersToHandledType: Map<KClass<*>, AbstractHandler<*>>
    val handlersToOwnType: Map<KClass<*>, AbstractHandler<*>>

    lateinit var context: VerificationContext

    init {
        factories[runtimeConfigurationName]?.forEach { (clazz, factory) ->
            configuration.instances[clazz] = factory().also { it.runtime = this }
        }

        factories[Predefined.NAME]?.forEach { (clazz, factory) ->
            predefined.instances[clazz] = factory().also { it.runtime = this }
        }

        handlersToHandledType = configuration.instances.toMutableMap().apply {
            predefined.instances.forEach { (clazz, instance) ->
                if (!this.containsKey(clazz)) {
                    put(clazz, instance)
                }
            }
        }

        handlersToOwnType = handlersToHandledType.map { (_, value) -> value::class to value }.toMap()

        handlersToHandledType.forEach { (_, handler) ->
            handler.addHooks()
        }
    }

    fun getHandlerFor(element: Any): AbstractHandler<*>? {
        return handlersToHandledType[element::class]
    }

    companion object {
        val factories = mutableMapOf<String, MutableMap<KClass<*>, () -> AbstractHandler<*>>>()
    }
}