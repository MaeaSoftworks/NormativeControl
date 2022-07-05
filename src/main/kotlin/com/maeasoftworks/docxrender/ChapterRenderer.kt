package com.maeasoftworks.docxrender

import com.maeasoftworks.docx4nc.parsers.ChapterParser
import com.maeasoftworks.docxrender.html.HTMLElement
import org.docx4j.TextUtils
import org.docx4j.wml.P
import org.docx4j.wml.P.Hyperlink
import org.docx4j.wml.R
import org.docx4j.wml.Text
import javax.xml.bind.JAXBElement

class ChapterRenderer(
    private val subparser: ChapterParser
) {
    private val html = HTMLElement("chapter")
    private var page = newPage
    private var isInner = false

    private var currentP: HTMLElement = HTMLElement("p")
    private var currentInner: HTMLElement? = null

    private var currentR: HTMLElement = HTMLElement("span")

    private val newPage: HTMLElement
        get() {
            page = HTMLElement("div").withClass("page")
            page.children.add(HTMLElement("div").withClass("page-size"))
            return page
        }

    fun render(): HTMLElement {
        html.children.add(page)
        for (p in subparser.chapter.content.indices) {
            when (subparser.chapter.content[p]) {
                is P -> renderP(subparser.chapter.content[p] as P)
            }
        }
        return html
    }

    private fun renderP(p: P) {
        currentP = HTMLElement("p")
        page.children.add(currentP)
        currentP.id = p.paraId
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

    private fun renderHyperlink(h: Hyperlink) {
        currentInner = HTMLElement("a")
        currentP.children.add(currentInner!!)
        for (r in h.content.indices) {
            when (h.content[r]) {
                is R -> renderR(h.content[r] as R)
            }
        }
        isInner = false
    }

    private fun renderR(r: R) {
        currentR = HTMLElement("span")
        if (isInner) {
            currentInner!!.children.add(currentR)
        } else {
            currentP.children.add(currentR)
        }
        for (c in r.content) {
            when (c) {
                is JAXBElement<*> -> {
                    when (c.value) {
                        is Text -> {
                            currentR.content.append(TextUtils.getText(c))
                        }
                        is R.LastRenderedPageBreak -> {
                            if (currentP.children.size == 1 && currentR.content.isEmpty()) {
                                page.children.removeLast()
                            }
                            page = newPage
                            html.children.add(page)
                            currentP = currentP.duplicate()
                            page.children.add(currentP)
                            currentR = HTMLElement("span")
                            if (isInner) {
                                currentInner = currentInner!!.duplicate()
                                currentP.children.add(currentInner!!)
                                currentInner!!.children.add(currentR)
                            } else {
                                currentP.children.add(currentR)
                            }
                        }
                    }
                }
            }
        }
    }
}