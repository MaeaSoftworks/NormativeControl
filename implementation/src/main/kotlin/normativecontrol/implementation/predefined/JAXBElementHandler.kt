package normativecontrol.implementation.predefined

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.HandlerMapper

internal class JAXBElementHandler : Handler<JAXBElement<*>>() {
    context(VerificationContext)
    override fun handle(element: JAXBElement<*>) {
        HandlerMapper[configuration, element.value]?.handleElement(element.value)
    }

    @HandlerFactory(JAXBElement::class, Predefined::class)
    companion object : Factory<JAXBElementHandler> {
        override fun create() = JAXBElementHandler()
    }
}