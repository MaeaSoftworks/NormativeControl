package normativecontrol.core

import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.HandlerCollection
import normativecontrol.core.handlers.HandlerMapper
import kotlin.reflect.KClass

class Runtime(
    val runtimeConfigurationName: String,
    val collectionFactories: Map<String, () -> HandlerCollection>
) {
    val configuration = (collectionFactories[runtimeConfigurationName] ?: throw Exception("Collection with name $runtimeConfigurationName not found"))
        .invoke() as? Configuration<*> ?: throw Exception("Collection with name $runtimeConfigurationName is not an Configuration")
    val predefined = (collectionFactories[HandlerMapper.PREDEFINED_NAME] ?: throw Exception("Predefined collection was not found")).invoke()
    val handlers: Map<KClass<*>, Handler<*>>

    init {
        HandlerMapper.factories[runtimeConfigurationName]?.forEach { (clazz, factory) ->
            configuration.instances[clazz] = (factory.create() as Handler<*>).also { it.runtime = this }
        }

        HandlerMapper.factories[HandlerMapper.PREDEFINED_NAME]?.forEach { (clazz, factory) ->
            predefined.instances[clazz] = (factory.create() as Handler<*>).also { it.runtime = this }
        }

        handlers = configuration.instances + predefined.instances

        handlers.forEach { (_, handler) ->
            handler.addHooks()
        }
    }
}