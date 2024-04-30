package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.handlers.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.Drawing

@Handler(Drawing::class, UrFUConfiguration::class)
internal class DrawingHandler : AbstractHandler<Drawing>(), StateProvider<UrFUState> {
    override fun addHooks() {
        runtime.handlers[PHandler::class]?.hooks?.afterHandle?.subscribe {
            with(runtime.context) {
                if (state.currentPWithDrawing) {
                    state.currentPWithDrawing = false
                    return@subscribe
                }
                state.sinceDrawing++
            }
        }
    }

    context(VerificationContext)
    override fun handle(element: Drawing) {
        state.currentPWithDrawing = true
        state.sinceDrawing = 0
    }
}