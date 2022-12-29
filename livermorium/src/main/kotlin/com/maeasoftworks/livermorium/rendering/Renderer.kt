package com.maeasoftworks.livermorium.rendering

import com.maeasoftworks.livermorium.model.css.properties.*
import com.maeasoftworks.livermorium.model.css.properties.Color
import com.maeasoftworks.livermorium.model.css.properties.FontFamily
import com.maeasoftworks.livermorium.model.html.HTMLElement
import com.maeasoftworks.polonium.parsers.DocumentParser
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
        val ppr = parser.propertiesStorage[p]
        currentP!!.style += {
            MarginLeft set ppr.ind?.left
            MarginRight set ppr.ind?.right
            MarginBottom set ppr.spacing?.after
            MarginTop set ppr.spacing?.before
            LineHeight set ppr.spacing?.line
            TextIndent set ppr.ind?.firstLine
            TextAlign set ppr.jc?.`val`
            BackgroundColor set ppr.shd?.fill
            Hyphens set !(ppr.suppressAutoHyphens?.isVal ?: false)
        }
        stylizeFromRPr(ppr.rPr)
    }

    /**
     * Creates CSS style for run
     * @param r run
     */
    private fun stylizeR(r: R) = stylizeFromRPr(parser.propertiesStorage[r])

    /**
     * Adds CSS style for paragraph by run style or creates CSS style for run
     * @param rpr run style
     */
    private fun stylizeFromRPr(rpr: RPrAbstract?) {
        (if (rpr is ParaRPr?) currentP else currentR)!!.style += {
            FontFamily set rpr?.rFonts
            FontSize set rpr?.sz?.`val`
            FontStyle set rpr?.i?.isVal
            FontWeight set rpr?.b?.isVal
            Color set rpr?.color?.`val`
            BackgroundColor set rpr?.highlight?.`val`
            TextTransform set rpr?.caps?.isVal
            FontVariantCaps set rpr?.smallCaps?.isVal
            FontVariantLigatures set rpr?.ligatures?.`val`
            LetterSpacing set rpr?.spacing?.`val`
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
