package ru.maeasoftworks.normativecontrol.core.abstractions.handlers

import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.implementations.predefined.BuiltInProfile

object HandlerMapper {
    private val implementedChains = mutableMapOf<Profile, MappingChain>()
    private val predefinedChain: MappingChain = mutableListOf()

    fun map(handlerConfig: HandlerConfig<*, *>) {
        if (handlerConfig.profile != BuiltInProfile) {
            if (!implementedChains.containsKey(handlerConfig.profile)) {
                implementedChains[handlerConfig.profile] = mutableListOf()
            }
            implementedChains[handlerConfig.profile]!! += handlerConfig
        } else {
            predefinedChain += handlerConfig
        }
    }

    /**
     * Searches applicable [Handler] in registered handlers. If no custom handlers was registered,
     * it will search in builtin mappings. And if no mappers was found, returns `null`.
     * @param profile current verification profile
     * @param target object that requires handler
     * @return mapped handler if found, else `null`
     */
    operator fun get(profile: Profile, target: Any): Handler<*, *>? {
        if (!implementedChains.containsKey(profile)) {
            throw IllegalArgumentException("Implementation didn't registered any handler")
        }
        return findHandlerOf(target, implementedChains[profile]!!) ?: findHandlerOf(target, predefinedChain)
    }

    private fun findHandlerOf(target: Any, mappingChain: MappingChain): Handler<*, *>? {
        for (mapping in mappingChain) {
            if (mapping.test(target)) {
                return mapping.handler()
            }
        }
        return null
    }
}

typealias MappingChain = MutableList<HandlerConfig<*, *>>