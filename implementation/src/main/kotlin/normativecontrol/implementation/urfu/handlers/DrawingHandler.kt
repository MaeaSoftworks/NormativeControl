package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.shared.debug
import org.docx4j.wml.Drawing
import org.slf4j.LoggerFactory

internal class DrawingHandler : Handler<Drawing>() {
    private val logger = LoggerFactory.getLogger(this::class.java)
    var sinceDrawing = -1
    var currentPWithDrawing = false

    override fun addHooks() {
        add {
            hook(PHandler::class, HookType.AfterHandle) {
                if (currentPWithDrawing) {
                    currentPWithDrawing = false
                    logger.debug { "currentPWithDrawing set to false" }
                    return@hook
                }
                sinceDrawing++
            }
        }
    }

    context(VerificationContext)
    override fun handle(element: Drawing) {
        currentPWithDrawing = true
        sinceDrawing = 0
        logger.debug { "currentPWithDrawing set to true" }
    }

    @HandlerFactory(Drawing::class, UrFUConfiguration::class)
    companion object : Factory<DrawingHandler> {
        override fun create() = DrawingHandler()
    }
}