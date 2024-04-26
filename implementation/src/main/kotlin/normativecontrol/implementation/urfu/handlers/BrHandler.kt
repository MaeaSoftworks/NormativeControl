package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.Br
import org.docx4j.wml.STBrType

@Handler(Br::class, UrFUConfiguration::class)
internal class BrHandler : AbstractHandler<Br>(), StateProvider<UrFUState> {
    context(VerificationContext)
    override fun handle(element: Br) {
        if (element.type == STBrType.PAGE) {
            state.rSinceBr = 0
            render.pageBreak(1)
        }
    }
}