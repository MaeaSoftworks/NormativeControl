package normativecontrol.core.rendering.html

import normativecontrol.core.contexts.RenderingContext
import normativecontrol.core.rendering.css.DeclarationBlock
import java.io.Serializable

/**
 * Representation of any HTML element.
 * @property type Type of element. If type is not exists in [HtmlElement.Type] use [HtmlElement.create].
 * @property hasClosingTag Specifies the element format: with closing tag (e.g. `<div></div>`) or without (e.g. `<button/>`)
 */
context(RenderingContext)
open class HtmlElement(val type: Type, private val hasClosingTag: Boolean = true) {
    /**
     * Equivalent of HTML `class` attribute.
     */
    val classes: MutableList<String> = mutableListOf()

    /**
     * HTML tag content.
     * E.g.:
     * ```html
     * <div>Content</div>
     * ```
     */
    var content: Serializable? = null

    /**
     * Inlined CSS style, equivalent for `style` attribute. E.g.:
     * ```html
     * <div style="display: block;"><div>
     * ```
     */
    var style: DeclarationBlock = DeclarationBlock(classes)

    /**
     * Other not predefined attributes of element.
     */
    val attributes: Attributes by lazy { Attributes() }

    /**
     * Child elements of this tag. E.g.:
     * ```html
     * <div>
     *     <button/>
     *     <span></span>
     * </div>
     * ```
     */
    val children by lazy { ElementChildren() }

    /**
     * Parent of this element. It is always `null` for `html` element.
     */
    var parent: HtmlElement? = null
        private set

    private var id: String? = null
    private var unsafeType: String? = null

    /**
     * Creates HtmlElement with custom (not predefined) element type.
     * @param type Type of element.
     * @param hasClosingTag Specifies the element format: with closing tag (e.g. `<div></div>`) or without (e.g. `<button/>`)
     */
    constructor(type: String, hasClosingTag: Boolean = true) : this(Type.CUSTOM, hasClosingTag) {
        unsafeType = type
    }

    init {
        classes += nextElementClasses
        nextElementClasses.clear()
    }

    /**
     * Creates copy of this [HtmlElement] (target) and its [parent] for [level] times.
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

    /**
     * Serializes html element to actual HTML code.
     */
    override fun toString(): String {
        // todo replace with loop due to possible stack overflow
        return if (hasClosingTag) {
            "<${serializeType()}${serializeId()}${serializeClasses()}${serializeStyle()}$attributes>${serializeChildren()}${serializeContent()}</${serializeType()}>"
        } else {
            "<${serializeType()}${serializeId()}${serializeClasses()}${serializeStyle()}$attributes>"
        }
    }

    fun addChild(child: HtmlElement) {
        this.children += child
        child.parent = this
    }

    @HtmlDsl
    inline fun div(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.div(body))
    }

    @HtmlDsl
    inline fun p(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.p(body))
    }

    @HtmlDsl
    @Suppress("unused")
    inline fun span(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.span(body))
    }

    @HtmlDsl
    inline fun head(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.head(body))
    }

    @HtmlDsl
    inline fun body(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.body(body))
    }

    @HtmlDsl
    inline fun style(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.style(body))
    }

    @HtmlDsl
    inline fun script(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.script(body))
    }

    @HtmlDsl
    inline fun label(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.label(body))
    }

    @HtmlDsl
    inline fun input(body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.input(body))
    }

    @HtmlDsl
    inline fun create(type: String, hasClosingTag: Boolean = true, body: HtmlElement.() -> Unit) {
        addChild(normativecontrol.core.rendering.html.create(type, hasClosingTag, body))
    }

    private fun serializeType(): String = if (type == Type.CUSTOM && unsafeType != null) unsafeType!! else type.serialName

    private fun serializeClasses(): String = if (classes.size > 0) " class='${classes.joinToString(" ")}'" else ""

    private fun serializeId(): String = if (id != null) " id='$id'" else ""

    private fun serializeChildren(): String = if (children.size > 0) children.list.joinToString("") { it.toString() } else ""

    private fun serializeStyle(): String = if (style.ruleCount > 0) style.toString().let { if (it != "") " style='$it'" else "" } else ""

    private fun serializeContent(): String = content?.toString() ?: ""

    private fun duplicate(): HtmlElement {
        return HtmlElement(this@HtmlElement.type).also {
            it.classes.addAll(classes)
            it.id = id
            it.style = style
        }
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

    @JvmInline
    value class Attributes(private val attributes: MutableList<String> = mutableListOf()) {
        /**
         * Add attribute without value. E.g.:
         * ```html
         * <input checked />
         * @receiver attribute name
         */
        @AttributesDsl
        operator fun String.unaryPlus() {
            attributes += " $this"
        }

        /**
         * Add attribute with value. E.g.:
         * ```html
         * </button type="submit">
         * ```
         */
        @AttributesDsl
        infix fun String.set(value: String) {
            attributes += " $this=\"$value\""
        }

        /**
         * Attributes builder, allows to use [String.set] and [String.unaryPlus] functions.
         * @param attributes attributes initializer
         */
        @AttributesDsl
        operator fun invoke(attributes: Attributes.() -> Unit) {
            attributes()
        }

        override fun toString(): String {
            return if (attributes.size > 0) " " + attributes.joinToString("") else ""
        }

        @DslMarker
        annotation class AttributesDsl
    }

    @JvmInline
    value class ElementChildren(private val children: MutableList<HtmlElement> = mutableListOf()) {
        val size: Int
            get() = children.size

        val list: List<HtmlElement>
            get() = children

        private fun add(element: HtmlElement) {
            children.add(element)
        }

        operator fun get(clazz: String): HtmlElement? {
            return children.firstOrNull { it.classes.contains(clazz.removePrefix(".")) }
        }

        operator fun get(pos: Int): HtmlElement? = children.getOrNull(pos)

        operator fun plusAssign(element: HtmlElement) = add(element)
    }
}