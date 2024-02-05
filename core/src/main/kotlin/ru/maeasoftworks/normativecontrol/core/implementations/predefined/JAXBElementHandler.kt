package ru.maeasoftworks.normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import ru.maeasoftworks.normativecontrol.core.abstractions.*
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

@EagerInitialization
object JAXBElementHandler : Handler<JAXBElement<*>>(Profile.BuiltIn, Mapping.of { JAXBElementHandler }) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as JAXBElement<*>
        HandlerMapper[profile, element.value]?.handle(element.value)
    }
}