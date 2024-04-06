package normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.contexts.VerificationContext

object JAXBElementHandler : Handler<JAXBElement<*>, Nothing, Nothing>() {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as JAXBElement<*>
        HandlerMapper[configuration, element.value]?.handle(element.value)
    }
}