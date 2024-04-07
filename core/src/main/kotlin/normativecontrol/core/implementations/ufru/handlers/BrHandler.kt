package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.annotations.ReflectHandler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUConfiguration.state as runState
import org.docx4j.wml.Br
import org.docx4j.wml.STBrType

@ReflectHandler(Br::class, UrFUConfiguration::class)
object BrHandler : Handler<Br> {
    context(VerificationContext)
    override fun handle(element: Br) {
        if (element.type == STBrType.PAGE) {
            runState.rSinceBr = 0
            render.pageBreak(1)
        }
    }
}