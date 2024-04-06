package normativecontrol.core.abstractions.handlers

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.implementations.predefined.BuiltInConfiguration

object HandlerMapper {
    private val implementedChains = mutableMapOf<Configuration, MappingChain>()
    private val predefinedChain: MappingChain = mutableListOf()

    fun map(handlerConfig: HandlerConfig<*, *>) {
        if (handlerConfig.configuration != BuiltInConfiguration) {
            if (!implementedChains.containsKey(handlerConfig.configuration)) {
                implementedChains[handlerConfig.configuration] = mutableListOf()
            }
            implementedChains[handlerConfig.configuration]!! += handlerConfig
        } else {
            predefinedChain += handlerConfig
        }
    }

    /**
     * Searches applicable [Handler] in registered handlers. If no custom handlers was registered,
     * it will search in builtin mappings. And if no mappers was found, returns `null`.
     * @param configuration current verification profile
     * @param target object that requires handler
     * @return mapped handler if found, else `null`
     */
    operator fun get(configuration: Configuration, target: Any): Handler<*, *>? {
        if (!implementedChains.containsKey(configuration)) {
            throw IllegalArgumentException("Implementation didn't registered any handler")
        }
        return findHandlerOf(target, implementedChains[configuration]!!) ?: findHandlerOf(target, predefinedChain)
    }

    private fun findHandlerOf(target: Any, mappingChain: MappingChain): Handler<*, *>? {
        return mappingChain.firstOrNull { it.test(target) }?.handler?.invoke()
    }
}

typealias MappingChain = MutableList<HandlerConfig<*, *>>