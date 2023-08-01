package com.maeasoftworks.rendering

import com.maeasoftworks.core.parsers.DocumentParser
import com.maeasoftworks.rendering.model.css.properties.*
import com.maeasoftworks.rendering.model.css.properties.Color
import com.maeasoftworks.rendering.model.css.properties.FontFamily
import com.maeasoftworks.rendering.model.html.HTMLElement
import jakarta.xml.bind.JAXBElement
import org.docx4j.TextUtils
import org.docx4j.wml.*
import org.docx4j.wml.P.Hyperlink

/**
 * Rendering main class
 * @param parser initialized document parser
 */
class Renderer(
    private val parser: DocumentParser
) {
    private val html = HTMLElement("div")
    private var currentPage: HTMLElement? = newPage
    private var isInner = false
    private var currentP: HTMLElement? = HTMLElement("p")
    private var currentR: HTMLElement? = HTMLElement("span")
    private var currentInner: HTMLElement? = null

    private val newPage: HTMLElement
        get() = HTMLElement("div").withClass("page")

    private val newP: HTMLElement
        get() = HTMLElement("p")

    private val newInner: HTMLElement
        get() = HTMLElement("a")

    private var lastPBeforePageBreak: HTMLElement? = null
    private var lastInnerBeforePageBreak: HTMLElement? = null
    private var alreadyBroken = false

    /**
     * Rendering entry point. Starts iteration on paragraphs.
     * @return rendered DOM in object representation
     */
    fun render(): HTMLElement {
        html.children.add(currentPage!!)
        for (p in parser.doc.content.indices) {
            when (parser.doc.content[p]) {
                is P -> renderP(parser.doc.content[p] as P)
            }
        }
        return html
    }

    /**
     * Function to refresh current paragraph & page after page break
     */
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

    /**
     * Detects document's content type and invokes renderers for this type
     * @param p paragraph
     */
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

    /**
     * Creates CSS style for paragraph
     * @param p paragraph
     */
    private fun stylizeP(p: P) {
        currentP!!.style += {
            MarginLeft set parser.resolver.getActualProperty(p) { ind?.left }
            MarginRight set parser.resolver.getActualProperty(p) { ind?.right }
            MarginBottom set parser.resolver.getActualProperty(p) { spacing?.after }
            MarginTop set parser.resolver.getActualProperty(p) { spacing?.before }
            LineHeight set parser.resolver.getActualProperty(p) { spacing?.line }
            TextIndent set parser.resolver.getActualProperty(p) { ind?.firstLine }
            TextAlign set parser.resolver.getActualProperty(p) { jc?.`val` }
            BackgroundColor set parser.resolver.getActualProperty(p) { shd?.fill }
            Hyphens set !(parser.resolver.getActualProperty(p) {suppressAutoHyphens?.isVal } ?: false)
        }
    }

    /**
     * Creates CSS style for run
     * @param r run
     */
    private fun stylizeR(r: R) {

        currentR!!.style += {
            FontFamily set parser.resolver.getActualProperty(r) { rFonts }
            FontSize set parser.resolver.getActualProperty(r) { sz?.`val` }
            FontStyle set parser.resolver.getActualProperty(r) { i?.isVal }
            FontWeight set parser.resolver.getActualProperty(r) { b?.isVal }
            Color set parser.resolver.getActualProperty(r) { color?.`val` }
            BackgroundColor set parser.resolver.getActualProperty(r) { highlight?.`val` }
            TextTransform set parser.resolver.getActualProperty(r) { caps?.isVal }
            FontVariantCaps set parser.resolver.getActualProperty(r) { smallCaps?.isVal }
            FontVariantLigatures set parser.resolver.getActualProperty(r) { ligatures?.`val` }
            LetterSpacing set parser.resolver.getActualProperty(r) { spacing?.`val` }
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

    /**
     * Detects paragraph's content type and invokes renderers for this type
     * @param r run
     */
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

    /**
     * Ends current page and creates new page
     */
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
