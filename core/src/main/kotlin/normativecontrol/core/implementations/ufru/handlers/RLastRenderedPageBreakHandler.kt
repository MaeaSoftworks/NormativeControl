package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUConfiguration.globalState
import org.docx4j.wml.R

object RLastRenderedPageBreakHandler : Handler<R.LastRenderedPageBreak, Nothing, Nothing>() {
    context(VerificationContext)
    override fun handle(element: Any) {
        if (globalState.rSinceBr > 2)
            render.pageBreak(1)
    }
}