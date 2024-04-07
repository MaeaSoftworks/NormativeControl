package normativecontrol.core.contexts

import normativecontrol.core.abstractions.mistakes.MistakeSerializer
import normativecontrol.core.rendering.css.Rule
import normativecontrol.core.rendering.css.Stylesheet
import normativecontrol.core.rendering.html.HtmlElement
import normativecontrol.core.rendering.html.createPageStyle
import normativecontrol.core.rendering.html.div
import normativecontrol.core.rendering.html.htmlTemplate
import normativecontrol.core.implementations.ufru.UrFUConfiguration.state as runState
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart

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
        runState.foldStylesheet(globalStylesheet)
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
