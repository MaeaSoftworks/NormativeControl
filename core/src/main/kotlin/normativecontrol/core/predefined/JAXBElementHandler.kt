package normativecontrol.core.predefined

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.handlers.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler

@Handler(JAXBElement::class, Predefined::class)
internal class JAXBElementHandler : AbstractHandler<JAXBElement<*>>() {
    context(VerificationContext)
    override fun handle(element: JAXBElement<*>) {
        runtime.handlers[element.value]?.handleElement(element.value)
    }
}