package normativecontrol.core.implementations.ufru.handlers

import org.docx4j.wml.Br
import org.docx4j.wml.STBrType
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerConfig
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUProfile
import normativecontrol.core.implementations.ufru.UrFUProfile.globalState

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