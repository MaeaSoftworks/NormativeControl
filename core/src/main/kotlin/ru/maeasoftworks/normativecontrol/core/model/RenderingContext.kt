package ru.maeasoftworks.normativecontrol.core.model

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.rendering.HtmlElement
import ru.maeasoftworks.normativecontrol.core.rendering.div
import ru.maeasoftworks.normativecontrol.core.rendering.htmlTemplate

class RenderingContext(doc: MainDocumentPart?) {
    private val html = htmlTemplate(doc)
    private val body = html.children[1]
    var currentPage: HtmlElement = createPage()
    val appender = Appender(currentPage)

    private fun createPage(): HtmlElement {
        return div { classes += "page" }.also { body.children += it }
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

    fun getString(): String {
        return html.toString()
    }

    inner class Appender(
        @PublishedApi
        internal var pointer: HtmlElement?
    ) {
        infix fun append(element: HtmlElement) {
            pointer?.children?.add(element)
            element.parent = pointer
        }

        inline fun inLastElementScope(fn: HtmlElement.() -> Unit) {
            openLastElementScope()
            pointer?.fn()
            closeLastElementScope()
        }

        fun openLastElementScope() {
            pointer = pointer?.children?.last()
        }

        fun closeLastElementScope() {
            pointer = pointer?.parent
        }
    }
}