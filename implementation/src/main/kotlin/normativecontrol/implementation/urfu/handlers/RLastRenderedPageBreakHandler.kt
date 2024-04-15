package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.R

internal class RLastRenderedPageBreakHandler : Handler<R.LastRenderedPageBreak>(), StateProvider<UrFUState> {
    context(VerificationContext)
    override fun handle(element: R.LastRenderedPageBreak) {
        if (state.rSinceBr > 2)
            render.pageBreak(1)
    }

    @HandlerFactory(RLastRenderedPageBreakHandler::class, UrFUConfiguration::class)
    companion object: Factory<RLastRenderedPageBreakHandler> {
        override fun create() = RLastRenderedPageBreakHandler()
    }
}