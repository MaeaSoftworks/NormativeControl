package ru.maeasoftworks.normativecontrol.core.rendering

import jakarta.xml.bind.JAXBElement
import org.docx4j.TextUtils
import org.docx4j.wml.Br
import org.docx4j.wml.P
import org.docx4j.wml.P.Hyperlink
import org.docx4j.wml.R
import org.docx4j.wml.Text
import ru.maeasoftworks.normativecontrol.core.parsers.DocumentVerifier
import ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties.*
import ru.maeasoftworks.normativecontrol.core.rendering.model.html.HtmlElement
import ru.maeasoftworks.normativecontrol.core.utils.getPropertyValue

class Renderer(
    private val parser: DocumentVerifier
) {
    private val html = HtmlElement("div")
    private var currentPage: HtmlElement? = newPage
    private var isInner = false
    private var currentP: HtmlElement? = HtmlElement("p")
    private var currentR: HtmlElement? = HtmlElement("span")
    private var currentInner: HtmlElement? = null

    private val newPage: HtmlElement
        get() = HtmlElement("div").withClass("page")

    private val newP: HtmlElement
        get() = HtmlElement("p")

    private val newInner: HtmlElement
        get() = HtmlElement("a")

    private var lastPBeforePageBreak: HtmlElement? = null
    private var lastInnerBeforePageBreak: HtmlElement? = null
    private var alreadyBroken = false

    suspend fun render(): HtmlElement {
        html.children.add(currentPage!!)
        for (p in parser.doc.content.indices) {
            when (parser.doc.content[p]) {
                is P -> renderP(parser.doc.content[p] as P)
            }
        }
        return html
    }

    private fun refreshCurrentP() {
        if (currentP == null) {
            currentP = lastPBeforePageBreak ?: newP
            lastPBeforePageBreak = null
            if (currentPage == null) {
                currentPage = newPage
                html.children.add(currentPage!!)
            }
            currentPage!!.children.add(currentP!!)
        }
    }

    private suspend fun renderP(p: P) {
        currentP = newP
        if (currentPage == null) {
            currentPage = newPage
            html.children.add(currentPage!!)
        }
        currentPage!!.children.add(currentP!!)
        currentP!!.id = p.paraId
        stylizeP(p)
        if (p.content.isEmpty()) {
            currentP!!.children.add(HtmlElement("br", false))
        }
        for (r in p.content.indices) {
            when (p.content[r]) {
                is R -> renderR(p.content[r] as R)
                is JAXBElement<*> -> {
                    when ((p.content[r] as JAXBElement<*>).value) {
                        is Hyperlink -> {
                            isInner = true
                            renderHyperlink((p.content[r] as JAXBElement<*>).value as Hyperlink)
                        }
                    }
                }
            }
        }
    }

    private suspend fun stylizeP(p: P) {
        currentP!!.style {
            marginLeft = MarginLeft(p.getPropertyValue { ind?.left }?.toDouble())
            marginRight = MarginRight(p.getPropertyValue { ind?.right }?.toDouble())
            marginBottom = MarginBottom(p.getPropertyValue { spacing?.after }?.toDouble())
            marginTop = MarginTop(p.getPropertyValue { spacing?.before }?.toDouble())
            lineHeight = LineHeight(p.getPropertyValue { spacing?.line }?.toDouble())
            textIndent = TextIndent(p.getPropertyValue { ind?.firstLine }?.toDouble())
            textAlign = TextAlign(p.getPropertyValue { jc?.`val` })
            backgroundColor = BackgroundColor(p.getPropertyValue { shd?.fill })
            hyphens = Hyphens(!(p.getPropertyValue { suppressAutoHyphens?.isVal } ?: false))
        }
    }

    private suspend fun stylizeR(r: R) {
        currentR!!.style {
            fontFamily = FontFamily(r.getPropertyValue { rFonts?.ascii })
            fontSize = FontSize(r.getPropertyValue { sz?.`val`?.toInt() })
            fontStyle = FontStyle(r.getPropertyValue { i?.isVal })
            fontWeight = FontWeight(r.getPropertyValue { b?.isVal })
            color = Color(r.getPropertyValue { color?.`val` })
            backgroundColor = BackgroundColor(r.getPropertyValue { highlight?.`val` })
            textTransform = TextTransform(r.getPropertyValue { caps?.isVal })
            fontVariantCaps = FontVariantCaps(r.getPropertyValue { smallCaps?.isVal })
            fontVariantLigatures = FontVariantLigatures(r.getPropertyValue { ligatures?.`val` })
            letterSpacing = LetterSpacing(r.getPropertyValue { spacing?.`val` }?.toDouble())
        }
    }

    private suspend fun renderHyperlink(h: Hyperlink) {
        currentInner = HtmlElement("a")
        refreshCurrentP()
        currentP!!.children.add(currentInner!!)
        for (r in h.content.indices) {
            when (h.content[r]) {
                is R -> renderR(h.content[r] as R)
            }
        }
        isInner = false
    }

    private suspend fun renderR(r: R) {
        for (c in r.content) {
            when (c) {
                is JAXBElement<*> -> {
                    when (c.value) {
                        is Text -> {
                            currentR = HtmlElement("span")
                            alreadyBroken = false
                            refreshCurrentP()
                            if (isInner) {
                                if (currentInner == null) {
                                    currentInner = lastInnerBeforePageBreak ?: newInner
                                    lastInnerBeforePageBreak = null
                                    currentP!!.children.add(currentInner!!)
                                }
                                currentInner!!.children.add(currentR!!)
                            } else {
                                currentP!!.children.add(currentR!!)
                            }
                            currentR!!.content.append(TextUtils.getText(c))
                            stylizeR(r)
                        }

                        is R.LastRenderedPageBreak -> {
                            if (!alreadyBroken) {
                                pageBreak()
                            }
                        }
                    }
                }

                is Br -> pageBreak()
            }
        }
    }

    private fun pageBreak() {
        lastPBeforePageBreak = currentP?.duplicate()
        lastInnerBeforePageBreak = currentInner?.duplicate()
        currentPage = null
        currentP = null
        currentInner = null
        currentR = null
        alreadyBroken = true
    }
}