package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Factory
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.abstractions.handlers.StateProvider
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUState
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