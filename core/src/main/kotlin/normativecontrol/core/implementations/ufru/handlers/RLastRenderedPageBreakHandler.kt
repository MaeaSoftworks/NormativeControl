package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerConfig
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUConfiguration.globalState
import org.docx4j.wml.R

@EagerInitialization
object RLastRenderedPageBreakHandler : Handler<R.LastRenderedPageBreak, Nothing>(
    HandlerConfig.create {
        setHandler { RLastRenderedPageBreakHandler }
        setTarget<R.LastRenderedPageBreak>()
        setProfile(UrFUConfiguration)
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        if (globalState.rSinceBr > 2)
            render.pageBreak(1)
    }
}