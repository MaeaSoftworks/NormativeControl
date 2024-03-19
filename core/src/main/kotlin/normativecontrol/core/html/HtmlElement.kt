package normativecontrol.core.html

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.css.Style
import java.io.Serializable

context(VerificationContext)
open class HtmlElement(
    val type: Type,
    private val hasClosingTag: Boolean = true,
) {
    val classes: MutableList<String> = mutableListOf()
    var id: String? = null
    var content: Serializable? = null
    var style: Style = Style(classes)
    val params: Params by lazy { Params() }
    private var unsafeType: String? = null
    val children by lazy { ElementChildren() }

    var parent: HtmlElement? = null
        private set

    init {
        id = mistakeUid
        if (mistakeUid != null) {
            classes += "m"
        }
        mistakeUid = null
    }

    constructor(type: String, hasClosingTag: Boolean = true) : this(Type.CUSTOM, hasClosingTag) {
        unsafeType = type
    }

    private fun serializeType(): String = if (type == Type.CUSTOM && unsafeType != null) unsafeType!! else type.serialName

    private fun serializeClasses(): String = if (classes.size > 0) " class='${classes.joinToString(" ")}'" else ""

    private fun serializeId(): String = if (id != null) " id='$id'" else ""

    private fun serializeChildren(): String = if (children.size > 0) children.list.joinToString("") { it.toString() } else ""

    private fun serializeStyle(): String = if (style.size > 0) style.toString().let { if (it != "") " style='$it'" else "" } else ""

    private fun serializeContent(): String = content?.toString() ?: ""

    override fun toString(): String {
        return if (hasClosingTag) {
            "<${serializeType()}${serializeId()}${serializeClasses()}${serializeStyle()}$params>${serializeChildren()}${serializeContent()}</${serializeType()}>"
        } else {
            "<${serializeType()}${serializeId()}${serializeClasses()}${serializeStyle()}$params>"
        }
    }

    private fun duplicate(): HtmlElement {
        return HtmlElement(this@HtmlElement.type).also {
            it.classes.addAll(classes)
            it.id = id
            it.style = style
        }
    }

    /**
     * Creates copy of this [HtmlElement] (target) and its [parent] [level] times.
     * @param level amount of parents to copy.
     * @return [Pair] of [HtmlElement], where first is copy of target [HtmlElement], second - parent at [level] level. Second can be first if [level] was 0.
     */
    fun duplicateUp(level: Int): Pair<HtmlElement, HtmlElement> {
        var target = this
        var firstCopy: HtmlElement? = null
        var copy: HtmlElement? = null
        var childCopy: HtmlElement? = null
        for (i in 0..level) {
            if (childCopy != null) {
                if (copy != null) {
                    childCopy = copy
                }
                copy = target.duplicate()
                copy.addChild(childCopy)
            } else {
                childCopy = target.duplicate()
                firstCopy = childCopy
            }
            target = target.parent ?: break
        }
        return firstCopy!! to (copy ?: firstCopy)
    }

    fun addChild(child: HtmlElement) {
        this.children += child
        child.parent = this
    }

    @HtmlDsl
    inline fun div(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.div(body))
    }

    @HtmlDsl
    inline fun p(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.p(body))
    }

    @HtmlDsl
    inline fun span(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.span(body))
    }

    @HtmlDsl
    inline fun head(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.head(body))
    }

    @HtmlDsl
    inline fun body(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.body(body))
    }

    @HtmlDsl
    inline fun style(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.style(body))
    }

    @HtmlDsl
    inline fun script(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.script(body))
    }

    @HtmlDsl
    inline fun label(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.label(body))
    }

    @HtmlDsl
    inline fun input(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.input(body))
    }

    @HtmlDsl
    inline fun create(type: String, hasClosingTag: Boolean = true, body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.html.create(type, hasClosingTag, body))
    }

    enum class Type(val serialName: String) {
        CUSTOM("custom"),
        DIV("div"),
        P("p"),
        BR("br"),
        SPAN("span"),
        HEAD("head"),
        BODY("body"),
        HTML("html"),
        STYLE("style"),
        SCRIPT("script"),
        LABEL("label"),
        INPUT("input")
    }

    inner class Params {
        private val params = mutableListOf<String>()
        operator fun String.unaryPlus() {
            params += " $this"
        }

        infix fun String.set(value: String) {
            params += " $this=\"$value\""
        }

        operator fun invoke(fn: Params.() -> Unit) {
            fn()
        }

        override fun toString(): String {
            return if (params.size > 0) " " + params.joinToString("") else ""
        }
    }

    @JvmInline
    value class ElementChildren(private val children: MutableList<HtmlElement> = mutableListOf()) {
        val size: Int
            get() = children.size

        val list: List<HtmlElement>
            get() = children

        fun add(element: HtmlElement) {
            children.add(element)
        }

        operator fun get(clazz: String): HtmlElement? {
            return children.firstOrNull { it.classes.contains(clazz.removePrefix(".")) }
        }

        operator fun get(pos: Int): HtmlElement? = children.getOrNull(pos)

        operator fun plusAssign(element: HtmlElement) = add(element)
    }
}