package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.HandlerMapper
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.abstractions.mapping
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.rendering.span
import ru.maeasoftworks.normativecontrol.core.utils.getPropertyValue
import ru.maeasoftworks.normativecontrol.core.utils.verificationContext

@EagerInitialization
object RHandler : Handler<R>({ register(Profile.UrFU, mapping<R> { RHandler }) }) {
    override suspend fun handle(element: Any) = verificationContext ctx@{
        element as R
        render.appender append span {
            style += {
                fontFamily set element.getPropertyValue { rFonts?.ascii }
                fontSize set element.getPropertyValue { sz?.`val` }?.toDouble()
                fontStyle set element.getPropertyValue { i?.isVal }
                fontWeight set element.getPropertyValue { b?.isVal }
                color set element.getPropertyValue { color?.`val` }
                backgroundColor set element.getPropertyValue { highlight?.`val` }
                textTransform set element.getPropertyValue { caps?.isVal }
                fontVariantCaps set element.getPropertyValue { smallCaps?.isVal }
                fontVariantLigatures set element.getPropertyValue { ligatures?.`val` }
                letterSpacing set element.getPropertyValue { spacing?.`val` }?.toDouble()
            }
        }
        render.appender.inLastElementScope {
            element.content.forEach {
                HandlerMapper[profile, it]?.handle(it)
            }
        }
    }
}