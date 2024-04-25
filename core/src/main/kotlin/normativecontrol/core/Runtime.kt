package normativecontrol.core

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.HandlerCollection
import kotlin.reflect.KClass

class Runtime(runtimeConfigurationName: String, collectionFactories: Map<String, () -> HandlerCollection>) {
    val configuration = (collectionFactories[runtimeConfigurationName] ?: throw Exception("Collection with name $runtimeConfigurationName not found"))
        .invoke() as? Configuration<*> ?: throw Exception("Collection with name $runtimeConfigurationName is not an Configuration")

    private val predefined = (collectionFactories[Core.PREDEFINED_NAME] ?: throw Exception("Predefined collection was not found")).invoke()

    val handlers: Map<KClass<*>, Handler<*>>

    lateinit var context: VerificationContext

    init {
        factories[runtimeConfigurationName]?.forEach { (clazz, factory) ->
            configuration.instances[clazz] = (factory.create() as Handler<*>).also { it.runtime = this }
        }

        factories[Core.PREDEFINED_NAME]?.forEach { (clazz, factory) ->
            predefined.instances[clazz] = (factory.create() as Handler<*>).also { it.runtime = this }
        }

        handlers = configuration.instances.toMutableMap().apply {
            predefined.instances.forEach { (clazz, instance) ->
                if (!this.containsKey(clazz)) {
                    put(clazz, instance)
                }
            }
        }

        handlers.forEach { (_, handler) ->
            handler.addHooks()
        }
    }

    fun getHandlerFor(element: Any): Handler<*>? {
        return handlers[element::class]
    }

    companion object {
        val factories = mutableMapOf<String, MutableMap<KClass<*>, Factory<*>>>()
    }
}