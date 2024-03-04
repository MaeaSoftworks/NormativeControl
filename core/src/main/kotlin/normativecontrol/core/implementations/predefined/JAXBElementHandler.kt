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
        setProfile(BuiltInProfile)
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as JAXBElement<*>
        HandlerMapper[profile, element.value]?.handle(element.value)
    }
}