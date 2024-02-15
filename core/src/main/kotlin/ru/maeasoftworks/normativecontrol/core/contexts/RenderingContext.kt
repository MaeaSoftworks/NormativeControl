package ru.maeasoftworks.normativecontrol.core.contexts

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.rendering.HtmlElement
import ru.maeasoftworks.normativecontrol.core.rendering.MistakeRenderer
import ru.maeasoftworks.normativecontrol.core.rendering.css.Rule
import ru.maeasoftworks.normativecontrol.core.rendering.css.Stylesheet
import ru.maeasoftworks.normativecontrol.core.rendering.div
import ru.maeasoftworks.normativecontrol.core.rendering.htmlTemplate

context(VerificationContext)
class RenderingContext(doc: MainDocumentPart?) {
    val mistakeRenderer = MistakeRenderer()
    val styleCache = mutableMapOf<Rule, String>()
    val globalStyle by lazy { html.children[0]!!.children.list.first { it.type == HtmlElement.Type.STYLE }.content as Stylesheet }
    private val html = htmlTemplate(doc, mistakeRenderer)
    private val root = html.children[1]!!.children[".container"]!!

    lateinit var currentPage: HtmlElement
        private set

    var pointer: HtmlElement? = null
        private set

    init {
        createPage()
    }

    /**
     * Breaks page and copies last element if needed.
     * @param copyingLevel level of element nesting that need to be copied, 0 to not copy anything.
     */
    fun pageBreak(copyingLevel: Int) {
        val (copy, parent) = pointer!!.duplicateUp(copyingLevel)
        createPage()
        currentPage.addChild(parent)
        pointer = copy
    }

    fun getString(): String {
        return html.toString()
    }

    infix fun append(element: HtmlElement) {
        pointer?.addChild(element)
    }

    inline fun inLastElementScope(fn: HtmlElement.() -> Unit) {
        openLastElementScope()
        pointer?.fn()
        closeLastElementScope()
    }

    fun openLastElementScope() {
        pointer = pointer?.children?.list?.lastOrNull() ?: pointer
    }

    fun closeLastElementScope() {
        pointer = pointer?.parent
    }

    private fun createPage() {
        val page = div { classes += "page" }
        root.addChild(page)
        currentPage = page
        pointer = page
    }
}
