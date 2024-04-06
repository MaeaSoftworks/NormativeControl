package normativecontrol.core.abstractions.handlers

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.abstractions.Configuration
import normativecontrol.core.implementations.predefined.PredefinedConfiguration

object HandlerMapper {
    /**
     * Searches applicable [Handler] in registered handlers. If no custom handlers was registered,
     * it will search in builtin mappings. And if no mappers was found, returns `null`.
     * @param configuration current verification profile
     * @param target object that requires handler
     * @return mapped handler if found, else `null`
     */
    operator fun get(configuration: Configuration, target: Any): Handler<*, *, *>? {
        return configuration.mapHandler(target) ?: PredefinedConfiguration.mapHandler(target)
    }
}