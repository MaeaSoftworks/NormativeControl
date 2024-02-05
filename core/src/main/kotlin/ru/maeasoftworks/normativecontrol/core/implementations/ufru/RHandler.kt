package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.abstractions.*
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import ru.maeasoftworks.normativecontrol.core.rendering.span
import ru.maeasoftworks.normativecontrol.core.utils.getPropertyValue

@EagerInitialization
object RHandler : Handler<R>(Profile.UrFU, Mapping.of { RHandler }) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as R
        render.appender append span {
            style += {
                fontFamily set element.rPr.getPropertyValue { rFonts?.ascii }
                fontSize set element.rPr.getPropertyValue { sz?.`val` }?.toDouble()
                fontStyle set element.rPr.getPropertyValue { i?.isVal }
                fontWeight set element.rPr.getPropertyValue { b?.isVal }
                color set element.rPr.getPropertyValue { color?.`val` }
                backgroundColor set element.rPr.getPropertyValue { highlight?.`val` }
                textTransform set element.rPr.getPropertyValue { caps?.isVal }
                fontVariantCaps set element.rPr.getPropertyValue { smallCaps?.isVal }
                fontVariantLigatures set element.rPr.getPropertyValue { ligatures?.`val` }
                letterSpacing set element.rPr.getPropertyValue { spacing?.`val` }?.toDouble()
            }
        }
        render.appender.inLastElementScope {
            element.content.forEach {
                HandlerMapper[profile, it]?.handle(it)
            }
        }
    }
}