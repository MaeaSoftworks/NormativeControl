package normativecontrol.core.implementations.ufru.handlers

import org.docx4j.wml.R
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerConfig
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.contexts.VerificationContext

@EagerInitialization
object RLastRenderedPageBreakHandler : Handler<R.LastRenderedPageBreak, Nothing>(
    HandlerConfig.create {
        setHandler { RLastRenderedPageBreakHandler }
        setTarget<R.LastRenderedPageBreak>()
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        if (globalState["rSinceBr"] as Int > 2)
            render.pageBreak(1)
    }
}