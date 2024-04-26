package normativecontrol.implementation.predefined

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler

@Handler(JAXBElement::class, Predefined::class)
internal class JAXBElementHandler : AbstractHandler<JAXBElement<*>>() {
    context(VerificationContext)
    override fun handle(element: JAXBElement<*>) {
        runtime.getHandlerFor(element.value)?.handleElement(element.value)
    }
}