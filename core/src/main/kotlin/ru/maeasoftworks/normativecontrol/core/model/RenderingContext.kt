package ru.maeasoftworks.normativecontrol.core.model

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.rendering.HtmlElement
import ru.maeasoftworks.normativecontrol.core.rendering.css.Rule
import ru.maeasoftworks.normativecontrol.core.rendering.css.Stylesheet
import ru.maeasoftworks.normativecontrol.core.rendering.div
import ru.maeasoftworks.normativecontrol.core.rendering.htmlTemplate

context(VerificationContext)
class RenderingContext(doc: MainDocumentPart?) {
    val mistakes = mutableListOf<DetailedMistake>()
    private val html = htmlTemplate(doc, mistakes)
    private val body = html.children[1]
    val styleCache = mutableMapOf<Rule, String>()
    val globalStyle = html.children[0].children.first { it.type == HtmlElement.Type.STYLE }.content as Stylesheet

    lateinit var currentPage: HtmlElement
        private set

    val appender = Appender()

    init {
        createPage()
    }

    context(VerificationContext)
    private fun createPage() {
        val page = div { classes += "page" }
        body.addChild(page)
        currentPage = page
        appender.pointer = page
    }

    /**
     * Breaks page and copies last element if needed.
     * @param copyingLevel level of element nesting that need to be copied, 0 to not copy anything.
     */
    fun pageBreak(copyingLevel: Int) {
        val (copy, parent) = appender.pointer!!.duplicateUp(copyingLevel)
        createPage()
        currentPage.addChild(parent)
        appender.pointer = copy
    }

    fun getString(): String {
        return html.toString()
    }

    inner class Appender {
        @PublishedApi
        internal var pointer: HtmlElement? = null

        infix fun append(element: HtmlElement) {
            pointer?.addChild(element)
        }

        inline fun inLastElementScope(fn: HtmlElement.() -> Unit) {
            openLastElementScope()
            pointer?.fn()
            closeLastElementScope()
        }

        fun openLastElementScope() {
            pointer = pointer?.children?.lastOrNull() ?: pointer
        }

        fun closeLastElementScope() {
            pointer = pointer?.parent
        }
    }
}