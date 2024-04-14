package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Factory
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.abstractions.handlers.StateProvider
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUState
import org.docx4j.wml.Br
import org.docx4j.wml.STBrType

class BrHandler : Handler<Br>(), StateProvider<UrFUState> {
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