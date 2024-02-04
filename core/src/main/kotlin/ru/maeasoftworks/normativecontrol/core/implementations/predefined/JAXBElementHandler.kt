package ru.maeasoftworks.normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import ru.maeasoftworks.normativecontrol.core.abstractions.*
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.utils.verificationContext

@EagerInitialization
object JAXBElementHandler : Handler<JAXBElement<*>>(Profile.BuiltIn, Mapping.of { JAXBElementHandler }) {
    override suspend fun handle(element: Any): Unit = verificationContext {
        element as JAXBElement<*>
        HandlerMapper[profile, element.value]?.handle(element.value)
    }
}