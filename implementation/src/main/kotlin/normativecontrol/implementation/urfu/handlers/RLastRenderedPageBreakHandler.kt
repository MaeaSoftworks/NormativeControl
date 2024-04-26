package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.R

@Handler(RLastRenderedPageBreakHandler::class, UrFUConfiguration::class)
internal class RLastRenderedPageBreakHandler : AbstractHandler<R.LastRenderedPageBreak>(), StateProvider<UrFUState> {
    context(VerificationContext)
    override fun handle(element: R.LastRenderedPageBreak) {
        if (state.rSinceBr > 2)
            render.pageBreak(1)
    }
}