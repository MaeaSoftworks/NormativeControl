package normativecontrol.core.abstractions.handlers

object HandlerMapper {
    private val implementedChains = mutableMapOf<String, MappingChain>()
    private val predefinedChain: MappingChain = mutableListOf()

    fun map(handlerConfig: HandlerConfig<*, *>) {
        if (handlerConfig.profile != "_predefined") {
            if (!implementedChains.containsKey(handlerConfig.profile)) {
                implementedChains[handlerConfig.profile ?: "_"] = mutableListOf()
            }
            implementedChains[handlerConfig.profile ?: "_"]!! += handlerConfig
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
    operator fun get(profile: String, target: Any): Handler<*, *>? {
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