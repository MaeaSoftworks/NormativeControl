package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.Drawing
import org.slf4j.LoggerFactory

internal class DrawingHandler : Handler<Drawing>(), StateProvider<UrFUState> {
    override fun addHooks() {
        hook<PHandler, _>(HookType.AfterHandle) {
            with(runtime.context) {
                if (state.currentPWithDrawing) {
                    state.currentPWithDrawing = false
                    return@hook
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

    @HandlerFactory(Drawing::class, UrFUConfiguration::class)
    companion object : Factory<DrawingHandler> {
        private val logger = LoggerFactory.getLogger(DrawingHandler::class.java)

        override fun create() = DrawingHandler()
    }
}