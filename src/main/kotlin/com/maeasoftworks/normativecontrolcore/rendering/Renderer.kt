package com.maeasoftworks.normativecontrolcore.rendering

import com.maeasoftworks.normativecontrolcore.core.parsers.DocumentParser
import com.maeasoftworks.normativecontrolcore.core.utils.getPropertyValue
import com.maeasoftworks.normativecontrolcore.rendering.model.css.properties.*
import com.maeasoftworks.normativecontrolcore.rendering.model.html.HTMLElement
import jakarta.xml.bind.JAXBElement
import org.docx4j.TextUtils
import org.docx4j.wml.Br
import org.docx4j.wml.P
import org.docx4j.wml.P.Hyperlink
import org.docx4j.wml.R
import org.docx4j.wml.Text

class Renderer(
    private val parser: DocumentParser
) {
    private val html = HTMLElement("div")
    private var currentPage: HTMLElement? = newPage
    private var isInner = false
    private var currentP: HTMLElement? = HTMLElement("p")
    private var currentR: HTMLElement? = HTMLElement("span")
    private var currentInner: HTMLElement? = null
    private val resolver = parser.resolver

    private val newPage: HTMLElement
        get() = HTMLElement("div").withClass("page")

    private val newP: HTMLElement
        get() = HTMLElement("p")

    private val newInner: HTMLElement
        get() = HTMLElement("a")

    private var lastPBeforePageBreak: HTMLElement? = null
    private var lastInnerBeforePageBreak: HTMLElement? = null
    private var alreadyBroken = false

    fun render(): HTMLElement {
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

    private fun renderP(p: P) {
        currentP = newP
        if (currentPage == null) {
            currentPage = newPage
            html.children.add(currentPage!!)
        }
        currentPage!!.children.add(currentP!!)
        currentP!!.id = p.paraId
        stylizeP(p)
        if (p.content.isEmpty()) {
            currentP!!.children.add(HTMLElement("br", false))
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

    private fun stylizeP(p: P) {
        currentP!!.style += {
            MarginLeft set p.getPropertyValue(resolver) { ind?.left?.toDouble() }
            MarginRight set p.getPropertyValue(resolver) { ind?.right?.toDouble() }
            MarginBottom set p.getPropertyValue(resolver) { spacing?.after?.toDouble() }
            MarginTop set p.getPropertyValue(resolver) { spacing?.before?.toDouble() }
            LineHeight set p.getPropertyValue(resolver) { spacing?.line?.toDouble() }
            TextIndent set p.getPropertyValue(resolver) { ind?.firstLine?.toDouble() }
            TextAlign set p.getPropertyValue(resolver) { jc?.`val` }
            BackgroundColor set p.getPropertyValue(resolver) { shd?.fill }
            Hyphens set !(p.getPropertyValue(resolver) { suppressAutoHyphens?.isVal } ?: false)
        }
    }

    private fun stylizeR(r: R) {
        currentR!!.style += {
            FontFamily set r.getPropertyValue(resolver) { rFonts?.ascii }
            FontSize set r.getPropertyValue(resolver) { sz?.`val`?.toInt() }
            FontStyle set r.getPropertyValue(resolver) { i?.isVal }
            FontWeight set r.getPropertyValue(resolver) { b?.isVal }
            Color set r.getPropertyValue(resolver) { color?.`val` }
            BackgroundColor set r.getPropertyValue(resolver) { highlight?.`val` }
            TextTransform set r.getPropertyValue(resolver) { caps?.isVal }
            FontVariantCaps set r.getPropertyValue(resolver) { smallCaps?.isVal }
            FontVariantLigatures set r.getPropertyValue(resolver) { ligatures?.`val` }
            LetterSpacing set r.getPropertyValue(resolver) { spacing?.`val`?.toDouble() }
        }
    }

    private fun renderHyperlink(h: Hyperlink) {
        currentInner = HTMLElement("a")
        refreshCurrentP()
        currentP!!.children.add(currentInner!!)
        for (r in h.content.indices) {
            when (h.content[r]) {
                is R -> renderR(h.content[r] as R)
            }
        }
        isInner = false
    }

    private fun renderR(r: R) {
        for (c in r.content) {
            when (c) {
                is JAXBElement<*> -> {
                    when (c.value) {
                        is Text -> {
                            currentR = HTMLElement("span")
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
