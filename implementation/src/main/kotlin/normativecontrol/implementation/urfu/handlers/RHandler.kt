package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.handlers.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.rendering.html.span
import normativecontrol.core.components.TextContainer
import normativecontrol.core.verifier
import normativecontrol.core.verifyBy
import normativecontrol.core.wrappers.RPr.Companion.resolver
import normativecontrol.implementation.urfu.Reason
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.R

@Handler(R::class, UrFUConfiguration::class)
internal class RHandler : AbstractHandler<R>(), StateProvider<UrFUState> {
    private val rules = Rules()
    private val text = Text(this)

    inner class Text(handler: RHandler) : TextContainer<RHandler>(handler)

    context(VerificationContext)
    override fun handle(element: R) {
        state.rSinceBr++
        val rPr = element.resolver().rPr
        render {
            append {
                span {
                    style += {
                        fontFamily set (rPr.rFonts.ascii verifyBy rules.fonts)
                        fontSize set (rPr.sz?.`val`?.toDouble() verifyBy rules.fontSize)
                        fontStyle set (rPr.i?.isVal verifyBy rules.italic)
                        fontWeight set (rPr.b?.isVal verifyBy rules.bold)
                        color set (rPr.color?.`val` verifyBy rules.color)
                        backgroundColor set rPr.highlight?.`val`
                        textTransform set rPr.caps?.isVal
                        fontVariantCaps set rPr.smallCaps?.isVal
                        fontVariantLigatures set rPr.ligatures?.`val`
                        letterSpacing set rPr.spacing?.`val`?.toDouble()
                    }
                }
            }
            inLastElementScope {
                element.content.forEach {
                    runtime.handlers[it]?.handleElement(it)
                }
            }
        }
    }

    private inner class Rules {
        val fonts = verifier<String?> {
            if (text.isBlank == true || state.isCodeBlock) return@verifier
            if (it != "Times New Roman") {
                return@verifier mistake(Reason.IncorrectFont)
            }
        }
        val italic = verifier<Boolean?> {
            if (text.isBlank == true || state.isCodeBlock) return@verifier
            if (it == true) {
                return@verifier mistake(Reason.Italic)
            }
        }
        val bold = verifier<Boolean?> {
            if (text.isBlank == true || state.isCodeBlock) return@verifier
            if (!state.isHeader && it == true) {
                return@verifier mistake(Reason.BoldText)
            }
        }
        val fontSize = verifier<Double?> {
            val size = it?.div(2) ?: 0.0
            if (state.isCodeBlock) {
                if (size > 14 || size < 8) {
                    return@verifier mistake(Reason.IncorrectFontSizeInCode, size.toString(), "8 - 14")
                } else return@verifier
            }
            if (size - 14 > 0.01) {
                return@verifier mistake(Reason.IncorrectFontSize, size.toString(), "14")
            }
        }
        val color = verifier<String?> {
            if (text.isBlank == true || state.isCodeBlock) return@verifier
            if (it != null && it != "000000") {
                return@verifier mistake(Reason.IncorrectTextColor, it.toString(), Reason.IncorrectTextColor.expected)
            }
        }
    }
}