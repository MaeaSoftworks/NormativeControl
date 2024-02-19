package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.wml.Br
import org.docx4j.wml.STBrType
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.HandlerConfig
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext

@EagerInitialization
object BrHandler : Handler<Br, Nothing>(
    HandlerConfig.create {
        setHandler { BrHandler }
        setTarget<Br>()
        setProfile(Profile.UrFU)
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as Br
        if (element.type == STBrType.PAGE) {
            getSharedStateAs<SharedState>().rSinceBr = 0
            render.pageBreak(1)
        }
    }
}