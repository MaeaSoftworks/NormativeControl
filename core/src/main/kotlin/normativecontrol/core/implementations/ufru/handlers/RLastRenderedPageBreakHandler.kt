package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.annotations.ReflectHandler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUConfiguration.runState
import org.docx4j.wml.R

@ReflectHandler(R.LastRenderedPageBreak::class, UrFUConfiguration::class)
object RLastRenderedPageBreakHandler : Handler<R.LastRenderedPageBreak> {
    context(VerificationContext)
    override fun handle(element: R.LastRenderedPageBreak) {
        if (runState.rSinceBr > 2)
            render.pageBreak(1)
    }
}