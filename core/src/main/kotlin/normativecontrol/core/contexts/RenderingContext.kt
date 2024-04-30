package normativecontrol.core.contexts

import normativecontrol.core.Runtime
import normativecontrol.core.mistakes.MistakeSerializer
import normativecontrol.core.rendering.css.Rule
import normativecontrol.core.rendering.css.Stylesheet
import normativecontrol.core.rendering.html.HtmlElement
import normativecontrol.core.rendering.html.createPageStyle
import normativecontrol.core.rendering.html.div
import normativecontrol.core.rendering.html.htmlTemplate

/**
 * Part of [Runtime] created at the beginning of file rendering.
 *
 * @param runtime backlink to current [Runtime]
 */
class RenderingContext(private val runtime: Runtime?) {
    // region don't move down
    /**
     * Last page's page style id.
     */
    var pageStyleId: Int = 0

    /**
     * Cache for style inlining.
     */
    val styleCache = mutableMapOf<Rule, String>()

    /**
     * Global stylesheet which will be defined at template initialization.
     * Should not be replaced by [globalStylesheet] due to initialization loop.
     */
    var externalGlobalStylesheet = Stylesheet()

    private val mistakeSerializer = MistakeSerializer()

    private var lastPageStyleId: String? = null
    // endregion

    private val html = htmlTemplate(runtime?.context?.doc, mistakeSerializer)
    private val root = html.children[1]!!.children[".container"]!!

    val globalStylesheet by lazy { html.children[0]!!.children.list.first { it.type == HtmlElement.Type.STYLE }.content as Stylesheet }

    var mistakeUid: String? = null

    val renderingSettings = runtime?.context?.configuration?.renderingSettings

    private lateinit var currentPage: HtmlElement

    var pointer: HtmlElement? = null
        private set

    init {
        createPage(createPageStyle(runtime?.context?.doc?.contents?.body?.sectPr).also { lastPageStyleId = it })
        runtime?.context?.onMistakeEvent?.subscribe { mistake ->
            val uid = "m${mistake.id}"
            mistakeUid = uid
            mistakeSerializer.addMistake(
                mistake.mistakeReason,
                uid,
                mistake.expected,
                mistake.actual
            )
        }
        foldStylesheet(globalStylesheet)
    }

    inline operator fun invoke(fn: RenderingContext.() -> Unit) = with(this) { fn() }

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

    fun render(): String {
        return html.toString()
    }

    fun append(element: () -> HtmlElement) {
        pointer?.addChild(element())
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

    fun foldStylesheet(target: Stylesheet) {
        target.fold(externalGlobalStylesheet)
        externalGlobalStylesheet = Stylesheet()
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
