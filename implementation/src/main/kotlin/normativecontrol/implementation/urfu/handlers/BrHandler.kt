package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.Br
import org.docx4j.wml.STBrType

internal class BrHandler : Handler<Br>(), StateProvider<UrFUState> {
    context(VerificationContext)
    override fun handle(element: Br) {
        if (element.type == STBrType.PAGE) {
            state.rSinceBr = 0
            render.pageBreak(1)
        }
    }

    @HandlerFactory(Br::class, UrFUConfiguration::class)
    companion object: Factory<BrHandler> {
        override fun create() = BrHandler()
    }
}