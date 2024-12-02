package normativecontrol.core.contexts

import normativecontrol.core.Document
import normativecontrol.core.Runtime
import normativecontrol.core.annotations.CoreInternal
import normativecontrol.core.mistakes.MistakeSerializer
import normativecontrol.core.rendering.css.Rule
import normativecontrol.core.rendering.css.Stylesheet
import normativecontrol.core.rendering.html.Constants
import normativecontrol.core.rendering.html.HtmlElement
import normativecontrol.core.rendering.html.Pages
import normativecontrol.core.rendering.html.div

/**
 * Part of [Runtime] created at the beginning of file rendering.
 *
 * @param runtime backlink to current [Runtime]
 */
class RenderingContext(private val runtime: Runtime?) {
    // region DON'T MOVE ALL MEMBERS DUE TO INITIALIZATION ORDER
    internal val nextElementClasses = mutableListOf<String>()

    /**
     * Last page's page style id.
     */
    internal var pageStyleId: Int = 0

    /**
     * Cache for style inlining.
     */
    internal val styleCache = mutableMapOf<Rule, String>()

    /**
     * Global stylesheet which will be defined at template initialization.
     * Should not be replaced by [globalStylesheet] due to initialization loop.
     */
    internal var externalGlobalStylesheet = Stylesheet()

    private val mistakeSerializer = MistakeSerializer()

    private var lastPageStyleId: String? = null
    // endregion

    private val html = Document.createInitialHtmlMarkup(runtime?.context?.doc, mistakeSerializer)
    private val root = html.children[1]!!.children[Constants.CONTAINER_SELECTOR]!!

    val globalStylesheet by lazy { html.children[0]!!.children.list.first { it.type == HtmlElement.Type.STYLE }.content as Stylesheet }

    internal val renderingSettings = runtime?.context?.configuration?.renderingSettings

    lateinit var currentPage: HtmlElement
        private set

    var pointer: HtmlElement? = null
        private set

    init {
        createPage(Pages.createPageStyle(runtime?.context?.doc?.contents?.body?.sectPr).also { lastPageStyleId = it })
        runtime?.context?.onMistakeEvent?.subscribe { mistake ->
            val mistakeUid = "${Constants.MISTAKE_NUMBERED_HTML_CLASS_PREFIX}${mistake.id}"
            mistakeSerializer.addMistake(
                mistake.mistakeReason,
                mistakeUid,
                mistake.expected,
                mistake.actual
            )
            nextElementClasses += mistakeUid
            nextElementClasses += Constants.MISTAKE_HTML_CLASS_NAME
        }
        joinStylesheet(globalStylesheet)
    }

    /**
     * Render creation entrypoint function.
     * @param rendering function that will render object. It will be called inside [RenderingContext]
     */
    inline operator fun invoke(rendering: RenderingContext.() -> Unit) = with(this) { rendering() }

    /**
     * Insert a page break into document render.
     * @param copyingLevel deepness of copying previous element parents
     * @param pageStyleId id of CSS page style
     */
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

    /**
     * Render document to string.
     * @return rendered document as string
     */
    fun render(): String {
        return html.toString()
    }

    /**
     * Appends [element] to the last element in rendering tree.
     * @param element element that need to be appended
     */
    fun append(element: () -> HtmlElement) {
        pointer?.addChild(element())
    }

    /**
     * Executes action inside last element in rendering tree.
     * @param action action that will be executed
     */
    @OptIn(CoreInternal::class)
    inline fun inLastElementScope(action: HtmlElement.() -> Unit) {
        openLastElementScope()
        pointer?.action()
        closeLastElementScope()
    }

    @CoreInternal
    fun openLastElementScope() {
        pointer = pointer?.children?.list?.lastOrNull() ?: pointer
    }

    @CoreInternal
    fun closeLastElementScope() {
        pointer = pointer?.parent
    }

    /**
     * Joins temporary stylesheet to [target] stylesheet.
     */
    // TODO: remove external stylesheet at all
    fun joinStylesheet(target: Stylesheet) {
        target.join(externalGlobalStylesheet)
        externalGlobalStylesheet = Stylesheet()
    }

    /**
     * Creates a new page in rendering tree.
     * @param pageStyleId id of CSS style for page
     * @return created page
     */
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
