package ru.maeasoftworks.normativecontrol.core.abstractions

object HandlerMapper {
    val builtInChain = mutableListOf<Mapping<*>>()
    val chain = mutableMapOf<Profile, MutableList<Mapping<*>>>()

    fun register(profile: Profile, mapping: Mapping<*>) {
        if (profile != Profile.BuiltIn) {
            if (!chain.containsKey(profile)) {
                chain[profile] = mutableListOf()
            }
            chain[profile]!! += mapping
        } else {
            builtInChain += mapping
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
        if (!chain.containsKey(profile)) {
            throw IllegalArgumentException("Implementation didn't registered any handler")
        }
        for (mapping in chain[profile]!!) {
            if (mapping.test(target)) {
                return mapping.handler()
            }
        }
        for (mapping in builtInChain) {
            if (mapping.test(target)) {
                return mapping.handler()
            }
        }
        return null
    }
}