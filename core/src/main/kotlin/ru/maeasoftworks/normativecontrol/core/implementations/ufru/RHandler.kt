package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.abstractions.Config
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.HandlerMapper
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.rendering.span
import ru.maeasoftworks.normativecontrol.core.utils.resolvedRPr

@EagerInitialization
object RHandler : Handler<R, Nothing>(
    Config.create {
        setHandler { RHandler }
        setTarget<R>()
        setProfile(Profile.UrFU)
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as R
        getSharedStateAs<SharedState>().rSinceBr++
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