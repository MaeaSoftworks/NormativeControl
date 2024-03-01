package ru.maeasoftworks.normativecontrol.core.implementations.ufru.handlers

import org.docx4j.wml.Br
import org.docx4j.wml.STBrType
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.HandlerConfig
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile.globalState

@EagerInitialization
object BrHandler : Handler<Br, Nothing>(
    HandlerConfig.create {
        setHandler { BrHandler }
        setTarget<Br>()
        setProfile(UrFUProfile)
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as Br
        if (element.type == STBrType.PAGE) {
            globalState.rSinceBr = 0
            render.pageBreak(1)
        }
    }
}