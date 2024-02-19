package ru.maeasoftworks.normativecontrol.core.contexts

import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import ru.maeasoftworks.normativecontrol.core.abstractions.mistakes.MistakeSerializer
import ru.maeasoftworks.normativecontrol.core.html.HtmlElement
import ru.maeasoftworks.normativecontrol.core.html.createPageStyle
import ru.maeasoftworks.normativecontrol.core.html.div
import ru.maeasoftworks.normativecontrol.core.html.htmlTemplate
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.RuntimeState
import ru.maeasoftworks.normativecontrol.core.css.Rule
import ru.maeasoftworks.normativecontrol.core.css.Stylesheet

context(VerificationContext)
class RenderingContext(doc: MainDocumentPart?) {
    val mistakeSerializer = MistakeSerializer()
    val styleCache = mutableMapOf<Rule, String>()
    val globalStylesheet by lazy { html.children[0]!!.children.list.first { it.type == HtmlElement.Type.STYLE }.content as Stylesheet }
    private val html = htmlTemplate(doc, mistakeSerializer)
    private val root = html.children[1]!!.children[".container"]!!
    private var lastPageStyleId: String? = null

    lateinit var currentPage: HtmlElement
        private set

    var pointer: HtmlElement? = null
        private set

    init {
        createPage(createPageStyle(doc?.contents?.body?.sectPr).also { lastPageStyleId = it })
        getSharedStateAs<RuntimeState>().foldStylesheet(globalStylesheet)
    }

    fun pageBreak(copyingLevel: Int, pageStyleId: String? = null) {
        if (copyingLevel != -1) {
            val (copy, parent) = pointer!!.duplicateUp(copyingLevel)
            createPage(pageStyleId ?: lastPageStyleId)
            currentPage.addChild(parent)
            pointer = copy
        } else {
            pointer = createPage(pageStyleId)
        }
        if (pageStyleId != null) {
            lastPageStyleId = pageStyleId
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
