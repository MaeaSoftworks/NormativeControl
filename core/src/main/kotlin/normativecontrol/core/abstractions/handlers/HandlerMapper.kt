package normativecontrol.core.abstractions.handlers

import normativecontrol.core.implementations.predefined.Predefined
import kotlin.reflect.KClass

object HandlerMapper {
    private val implementedChains = mutableMapOf<HandlerCollection, MutableMap<KClass<*>, Handler<*>>>()
    private val predefinedChain = mutableMapOf<KClass<*>, Handler<*>>()

    fun add(configuration: HandlerCollection, target: KClass<*>, handler: Handler<*>) {
        if (configuration != Predefined) {
            if (!implementedChains.containsKey(configuration)) {
                implementedChains[configuration] = mutableMapOf()
            }
            implementedChains[configuration]!! += target to handler
        } else {
            predefinedChain += target to handler
        }
    }

    /**
     * Searches applicable [StatefulHandler] in registered handlers. If no custom handlers was registered,
     * it will search in builtin mappings. And if no mappers was found, returns `null`.
     * @param configuration current verification profile
     * @param target object that requires handler
     * @return mapped handler if found, else `null`
     */
    operator fun get(configuration: HandlerCollection, target: Any): Handler<*>? {
        if (!implementedChains.containsKey(configuration)) {
            throw IllegalArgumentException("Implementation didn't registered any handler")
        }
        return findHandlerOf(target, implementedChains[configuration]!!) ?: findHandlerOf(target, predefinedChain)
    }

    private fun findHandlerOf(target: Any, mappingChain: MutableMap<KClass<*>, Handler<*>>): Handler<*>? {
        return mappingChain[target::class]
    }
}