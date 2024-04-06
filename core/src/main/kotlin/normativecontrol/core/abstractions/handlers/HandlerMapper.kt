package normativecontrol.core.abstractions.handlers

import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.implementations.predefined.BuiltInConfiguration
import kotlin.reflect.KClass

object HandlerMapper {
    private val implementedChains = mutableMapOf<Configuration, MutableMap<KClass<*>, AbstractHandler>>()
    private val predefinedChain = mutableMapOf<KClass<*>, AbstractHandler>()

    fun add(configuration: Configuration, target: KClass<*>, handler: AbstractHandler) {
        if (configuration != BuiltInConfiguration) {
            if (!implementedChains.containsKey(configuration)) {
                implementedChains[configuration] = mutableMapOf()
            }
            implementedChains[configuration]!! += target to handler
        } else {
            predefinedChain += target to handler
        }
    }

    /**
     * Searches applicable [AbstractHandler] in registered handlers. If no custom handlers was registered,
     * it will search in builtin mappings. And if no mappers was found, returns `null`.
     * @param configuration current verification profile
     * @param target object that requires handler
     * @return mapped handler if found, else `null`
     */
    operator fun get(configuration: Configuration, target: Any): AbstractHandler? {
        if (!implementedChains.containsKey(configuration)) {
            throw IllegalArgumentException("Implementation didn't registered any handler")
        }
        return findHandlerOf(target, implementedChains[configuration]!!) ?: findHandlerOf(target, predefinedChain)
    }

    private fun findHandlerOf(target: Any, mappingChain: MutableMap<KClass<*>, AbstractHandler>): AbstractHandler? {
        return mappingChain[target::class]
    }
}