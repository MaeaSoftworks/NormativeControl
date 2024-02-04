package ru.maeasoftworks.normativecontrol.core.abstractions

import java.util.Optional
import kotlin.jvm.optionals.getOrNull

object HandlerMapper {
    private val implementedChains = mutableMapOf<Profile, MappingChain>()
    private val predefinedChain: MappingChain = mutableListOf()

    fun map(profile: Profile, mapping: Mapping<*>) {
        if (profile != Profile.BuiltIn) {
            if (!implementedChains.containsKey(profile)) {
                implementedChains[profile] = mutableListOf()
            }
            implementedChains[profile]!! += mapping
        } else {
            predefinedChain += mapping
        }
    }

    /**
     * Searches applicable [Handler] in registered handlers. If no custom handlers was registered,
     * it will search in builtin mappings. And if no mappers was found, returns `null`.
     * @param profile current verification profile
     * @param target object that requires handler
     * @return mapped handler if found, else `null`
     */
    operator fun get(profile: Profile, target: Any): Handler<*>? {
        if (!implementedChains.containsKey(profile)) {
            throw IllegalArgumentException("Implementation didn't registered any handler")
        }
        return findHandlerOf(target, implementedChains[profile]!!).orElse(findHandlerOf(target, predefinedChain).getOrNull())
    }

    private fun findHandlerOf(target: Any, mappingChain: MappingChain): Optional<Handler<*>> {
        for (mapping in mappingChain) {
            if (mapping.test(target)) {
                return Optional.of(mapping.handler())
            }
        }
        return Optional.empty()
    }
}

typealias MappingChain = MutableList<Mapping<*>>