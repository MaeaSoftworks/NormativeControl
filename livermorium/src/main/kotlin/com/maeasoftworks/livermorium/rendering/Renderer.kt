package com.maeasoftworks.livermorium.rendering

import com.maeasoftworks.livermorium.model.html.HTMLElement
import com.maeasoftworks.livermorium.rendering.converters.ColorNameConverter
import com.maeasoftworks.livermorium.rendering.converters.HexColorConverter
import com.maeasoftworks.livermorium.rendering.projectors.*
import com.maeasoftworks.livermorium.utils.PIXELS_IN_POINT
import com.maeasoftworks.livermorium.utils.POINTS_IN_LINES
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
            "margin-left" to ppr.ind?.left?.toDouble()?.div(PIXELS_IN_POINT) with "px"
            "margin-right" to ppr.ind?.right?.toDouble()?.div(PIXELS_IN_POINT) with "px"
            "margin-bottom" to ppr.spacing?.after?.toDouble()?.div(PIXELS_IN_POINT) with "px"
            "margin-top" to ppr.spacing?.before?.toDouble()?.div(PIXELS_IN_POINT) with "px"
            "line-height" set ppr.spacing?.line?.toDouble()?.div(POINTS_IN_LINES).toString()
            "text-indent" to ppr.ind?.firstLine?.toDouble()?.div(PIXELS_IN_POINT) with "px"
            "text-align" to ppr.jc?.`val` with JustifyProjector
            "background-color" to ppr.shd?.fill.toString() with HexColorConverter
            "hyphen" to !(ppr.suppressAutoHyphens?.isVal ?: false) with AutoHyphenProjector
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
        val target = if (rpr is ParaRPr) currentP!! else currentR!!
        target.style += {
            "font-family" to rpr?.rFonts with FontProjector
            "font-size" to rpr?.sz?.`val`?.toInt()?.div(2) with "px"
            "font-style" to rpr?.i?.isVal with ItalicProjector
            "font-weight" to rpr?.b?.isVal with WeightProjector
            "color" to rpr?.color?.`val`.toString() with HexColorConverter
            "background-color" to rpr?.highlight?.`val`.toString() with ColorNameConverter
            "text-transform" to rpr?.caps?.isVal with CapsProjector
            "font-variant-caps" to rpr?.smallCaps?.isVal with SmallCapsProjector
            "font-variant-ligatures" to rpr?.ligatures?.`val` with LigaturesProjector //todo: fix all time null
            "letter-spacing" to rpr?.spacing?.`val`?.toDouble()?.div(PIXELS_IN_POINT) with "px"
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