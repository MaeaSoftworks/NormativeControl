package normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.abstractions.handlers.Factory
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.contexts.VerificationContext

internal class JAXBElementHandler : Handler<JAXBElement<*>>() {
    context(VerificationContext)
    override fun handle(element: JAXBElement<*>) {
        HandlerMapper[configuration, element.value]?.handleElement(element.value)
    }

    @HandlerFactory(JAXBElement::class, Predefined::class)
    companion object: Factory<JAXBElementHandler> {
        override fun create() = JAXBElementHandler()
    }
}