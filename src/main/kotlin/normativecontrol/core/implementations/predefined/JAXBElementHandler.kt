package normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerConfig
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.contexts.VerificationContext

@EagerInitialization
object JAXBElementHandler : Handler<JAXBElement<*>, Nothing>(
    HandlerConfig.create {
        setHandler { JAXBElementHandler }
        setTarget<JAXBElement<*>>()
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as JAXBElement<*>
        HandlerMapper["_predefined", element.value]?.handle(element.value)
    }
}