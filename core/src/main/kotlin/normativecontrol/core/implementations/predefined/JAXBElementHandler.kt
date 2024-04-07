package normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.annotations.ReflectHandler
import normativecontrol.core.contexts.VerificationContext

@ReflectHandler(JAXBElement::class, Predefined::class)
object JAXBElementHandler : Handler<JAXBElement<*>> {
    context(VerificationContext)
    override fun handle(element: JAXBElement<*>) {
        HandlerMapper[configuration, element.value]?.handleElement(element.value)
    }
}