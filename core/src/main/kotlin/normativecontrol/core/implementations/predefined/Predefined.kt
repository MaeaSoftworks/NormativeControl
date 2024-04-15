package normativecontrol.core.implementations.predefined

import normativecontrol.core.abstractions.handlers.HandlerCollection
import normativecontrol.core.annotations.HandlerGroup

@HandlerGroup(Predefined.NAME)
internal class Predefined : HandlerCollection(NAME) {
    companion object {
        const val NAME = "__PREDEFINED"
    }
}