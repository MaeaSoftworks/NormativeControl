package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.rendering.html.span
import normativecontrol.core.verifier
import normativecontrol.core.verifyBy
import normativecontrol.core.wrappers.RPr.Companion.resolve
import normativecontrol.implementation.urfu.Reason
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.R

@Handler(R::class, UrFUConfiguration::class)
internal class RHandler : AbstractHandler<R>(), StateProvider<UrFUState> {
    private val rules = Rules()

    context(VerificationContext)
    override fun handle(element: R) {
        state.rSinceBr++
        val rPr = element.rPr.resolve()
        render append span {
            style += {
                fontFamily set (rPr.rFonts.ascii verifyBy rules.fonts)
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
                runtime.getHandlerFor(it)?.handleElement(it)
            }
        }
    }

    private inner class Rules {
        val fonts = verifier<String?> {
            if (it != "Times New Roman") {
                mistake(Reason.IncorrectFont)
            }
        }
    }
}