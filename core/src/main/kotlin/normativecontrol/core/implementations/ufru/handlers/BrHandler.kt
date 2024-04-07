package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.AbstractHandler
import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUConfiguration.runState
import org.docx4j.wml.Br
import org.docx4j.wml.STBrType

@Handler(Br::class, UrFUConfiguration::class)
object BrHandler : AbstractHandler() {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as Br
        if (element.type == STBrType.PAGE) {
            runState.rSinceBr = 0
            render.pageBreak(1)
        }
    }
}