package normativecontrol.core.predefined

import normativecontrol.core.annotations.Configuration
import normativecontrol.core.handlers.HandlerCollection

@Configuration(Predefined.NAME)
internal class Predefined : HandlerCollection(NAME) {
    companion object {
        internal const val NAME = "__PREDEFINED"
    }
}