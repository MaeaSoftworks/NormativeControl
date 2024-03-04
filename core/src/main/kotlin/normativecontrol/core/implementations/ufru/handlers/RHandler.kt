package normativecontrol.core.implementations.ufru.handlers

import org.docx4j.wml.R
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerConfig
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.UrFUProfile
import normativecontrol.core.html.span
import normativecontrol.core.implementations.ufru.UrFUProfile.globalState
import normativecontrol.core.utils.resolvedRPr

@EagerInitialization
object RHandler : Handler<R, Nothing>(
    HandlerConfig.create {
        setHandler { RHandler }
        setTarget<R>()
        setProfile(UrFUProfile)
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as R
        globalState.rSinceBr++
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
                HandlerMapper[profile, it]?.handle(it)
            }
        }
    }
}