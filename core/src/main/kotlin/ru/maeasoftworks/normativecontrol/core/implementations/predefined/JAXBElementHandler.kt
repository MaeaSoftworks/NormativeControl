package ru.maeasoftworks.normativecontrol.core.implementations.predefined

import jakarta.xml.bind.JAXBElement
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.HandlerMapper
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.utils.verificationContext
import me.prmncr.hotloader.HotLoaded

@HotLoaded
object JAXBElementHandler : Handler<JAXBElement<*>>({ register<JAXBElement<*>>(Profile.BuiltIn) { JAXBElementHandler } }) {
    override suspend fun handle(element: Any): Unit = verificationContext {
        HandlerMapper[profile, element]?.handle(element)
    }
}