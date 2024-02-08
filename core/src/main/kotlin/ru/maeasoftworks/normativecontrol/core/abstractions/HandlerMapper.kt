package ru.maeasoftworks.normativecontrol.core.abstractions

object HandlerMapper {
    private val implementedChains = mutableMapOf<Profile, MappingChain>()
    private val predefinedChain: MappingChain = mutableListOf()

    fun map(config: Config<*, *>) {
        if (config.profile != Profile.BuiltIn) {
            if (!implementedChains.containsKey(config.profile)) {
                implementedChains[config.profile] = mutableListOf()
            }
            implementedChains[config.profile]!! += config
        } else {
            predefinedChain += config
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

typealias MappingChain = MutableList<Config<*, *>>