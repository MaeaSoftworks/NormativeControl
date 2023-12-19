package ru.maeasoftworks.normativecontrol.core.abstractions

object HandlerMapper {
    val builtInChain = mutableListOf<Mapping<*>>()
    val chain = mutableMapOf<Profile, MutableList<Mapping<*>>>()

    inline fun <reified T> register(profile: Profile, noinline obj: HandlerInvocation<*>) {
        if (profile != Profile.BuiltIn) {
            if (!chain.containsKey(profile)) {
                chain[profile] = mutableListOf()
            }
            chain[profile]!! += { if (it is T) obj else { -> null } }
        } else {
            builtInChain += { if (it is T) obj else { -> null } }
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
        for (lambda in chain[profile]!!) {
            val maybeHandler = lambda(target)
            if (maybeHandler != null) {
                return maybeHandler()
            }
        }
        for (lambda in builtInChain) {
            val maybeHandler = lambda(target)
            if (maybeHandler != null) {
                return maybeHandler()
            }
        }
        return null
    }
}

typealias HandlerInvocation<T> = () -> Handler<T>?

typealias Mapping<T> = (Any) -> HandlerInvocation<T>?