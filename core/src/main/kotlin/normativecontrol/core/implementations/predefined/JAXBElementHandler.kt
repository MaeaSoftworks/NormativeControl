package normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.abstractions.handlers.AbstractHandler
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext

@Handler(JAXBElement::class, BuiltInConfiguration::class)
object JAXBElementHandler : AbstractHandler() {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as JAXBElement<*>
        HandlerMapper[configuration, element.value]?.handle(element.value)
    }
}