package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.AbstractHandler
import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUConfiguration.runState
import org.docx4j.wml.R

@Handler(R.LastRenderedPageBreak::class, UrFUConfiguration::class)
object RLastRenderedPageBreakHandler : AbstractHandler() {
    context(VerificationContext)
    override fun handle(element: Any) {
        if (runState.rSinceBr > 2)
            render.pageBreak(1)
    }
}