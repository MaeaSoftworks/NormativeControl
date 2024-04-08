package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.annotations.ReflectHandler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.rendering.html.span
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUConfiguration.runState
import normativecontrol.core.wrappers.resolvedRPr
import org.docx4j.wml.R

@ReflectHandler(R::class, UrFUConfiguration::class)
object RHandler : Handler<R> {
    context(VerificationContext)
    override fun handle(element: R) {
        runState.rSinceBr++
        val rPr = element.resolvedRPr
        render append span {
            style += {
                fontFamily set rPr.rFonts.ascii
                fontSize set rPr.sz?.`val`?.toDouble()
                fontStyle set rPr.i?.isVal
                fontWeight set rPr.b?.isVal
                color set rPr.color?.`val`
                backgroundColor set rPr.highlight?.`val`
                textTransform set rPr.caps?.isVal
                fontVariantCaps set rPr.smallCaps?.isVal
                fontVariantLigatures set rPr.ligatures?.`val`
                letterSpacing set rPr.spacing?.`val`?.toDouble()
            }
        }
        render.inLastElementScope {
            element.content.forEach {
                HandlerMapper[configuration, it]?.handleElement(it)
            }
        }
    }
}