package ru.maeasoftworks.normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.HandlerMapper
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.abstractions.mapping
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.utils.verificationContext

@EagerInitialization
object JAXBElementHandler : Handler<JAXBElement<*>>({ register(Profile.BuiltIn, mapping<JAXBElement<*>> { JAXBElementHandler }) }) {
    override suspend fun handle(element: Any): Unit = verificationContext {
        element as JAXBElement<*>
        HandlerMapper[profile, element.value]?.handle(element.value)
    }
}