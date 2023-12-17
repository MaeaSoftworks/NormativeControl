package ru.maeasoftworks.normativecontrol.core.model

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.rendering.htmlTemplate
import ru.maeasoftworks.normativecontrol.core.rendering.model.html.HtmlElement

class RenderingContext(doc: MainDocumentPart?) {
    val html = htmlTemplate(doc)
    val body = html.children[1]
    var currentPage: HtmlElement = createPage()

    private fun createPage(): HtmlElement {
        return ru.maeasoftworks.normativecontrol.core.rendering.model.html.div { classes += "page" }.also { body.children += it }
    }

    /**
     * Breaks page and copies last element if needed.
     * @param copyingLevel level of element nesting that need to be copied, 0 to not copy anything.
     */
    fun pageBreak(copyingLevel: Int) {
        var root = currentPage
        currentPage = createPage()
        var newRoot = currentPage
        for (i in 1..copyingLevel) {
            newRoot.children += root.children.last().also { root = it }.duplicate().also { newRoot = it }
        }
    }
}