package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Factory
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.shared.debug
import org.docx4j.wml.Drawing
import org.slf4j.LoggerFactory

internal class DrawingHandler: Handler<Drawing>() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    context(VerificationContext)
    override fun handle(element: Drawing) {
        logger.debug { "Found drawing at $pointer" }
    }

    @HandlerFactory(Drawing::class, UrFUConfiguration::class)
    companion object: Factory<DrawingHandler> {
        override fun create() = DrawingHandler()
    }
}