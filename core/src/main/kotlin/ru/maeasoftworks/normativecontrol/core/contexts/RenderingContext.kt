package ru.maeasoftworks.normativecontrol.core.contexts

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.SharedState
import ru.maeasoftworks.normativecontrol.core.rendering.*
import ru.maeasoftworks.normativecontrol.core.rendering.css.Rule
import ru.maeasoftworks.normativecontrol.core.rendering.css.Stylesheet

context(VerificationContext)
class RenderingContext(doc: MainDocumentPart?) {
    val mistakeRenderer = MistakeRenderer()
    val styleCache = mutableMapOf<Rule, String>()
    val globalStylesheet by lazy { html.children[0]!!.children.list.first { it.type == HtmlElement.Type.STYLE }.content as Stylesheet }
    private val html = htmlTemplate(doc, mistakeRenderer)
    private val root = html.children[1]!!.children[".container"]!!

    lateinit var currentPage: HtmlElement
        private set

    var pointer: HtmlElement? = null
        private set

    init {
        createPage(createPageStyle(doc?.contents?.body?.sectPr))
        getSharedStateAs<SharedState>().foldStylesheet(globalStylesheet)
    }

    fun pageBreak(copyingLevel: Int, pageStyleId: String? = null) {
        if (copyingLevel != -1) {
            val (copy, parent) = pointer!!.duplicateUp(copyingLevel)
            createPage(pageStyleId)
            currentPage.addChild(parent)
            pointer = copy
        }
        else {
            pointer = createPage(pageStyleId)
        }
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

    private fun createPage(pageStyleId: String? = null): HtmlElement {
        val page = div {
            classes += "page"
            if (pageStyleId != null) {
                classes += pageStyleId
            }
        }
        root.addChild(page)
        currentPage = page
        pointer = page
        return page
    }
}
