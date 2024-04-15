package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.*
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.rendering.html.span
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUState
import normativecontrol.core.wrappers.resolvedRPr
import org.docx4j.wml.R

internal class RHandler : Handler<R>(), StateProvider<UrFUState> {
    context(VerificationContext)
    override fun handle(element: R) {
        state.rSinceBr++
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

    @HandlerFactory(R::class, UrFUConfiguration::class)
    companion object: Factory<RHandler> {
        override fun create() = RHandler()
    }
}