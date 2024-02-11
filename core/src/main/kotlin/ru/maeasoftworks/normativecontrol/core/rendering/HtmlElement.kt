package ru.maeasoftworks.normativecontrol.core.rendering

import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import ru.maeasoftworks.normativecontrol.core.rendering.css.Style
import ru.maeasoftworks.normativecontrol.core.rendering.css.Stylesheet
import java.io.Serializable

context(VerificationContext)
open class HtmlElement(
    val type: Type,
    private val hasClosingTag: Boolean = true,
) {
    val classes: MutableList<String> = mutableListOf()
    var id: String? = null
    var content: Serializable? = null
    var style: Style = Style()

    val children: List<HtmlElement>
        get() = _children

    var parent: HtmlElement? = null
        private set

    private val _children: MutableList<HtmlElement> = mutableListOf()

    init {
        id = mistakeUid
        mistakeUid = null
    }

    private fun serializeClasses(): String = if (classes.size > 0) " class='${classes.joinToString(" ")}'" else ""

    private fun serializeId(): String = if (id != null) " id='$id'" else ""

    private fun serializeChildren(): String = if (_children.size > 0) _children.joinToString("") { it.toString() } else ""

    private fun serializeStyle(): String = if (style.size > 0) style.toString().let { if (it != "") " style='$it'" else "" } else ""

    private fun serializeContent(): String = content?.toString() ?: ""

    override fun toString(): String {
        return if (hasClosingTag) {
            "<${type.serialName}${serializeId()}${serializeClasses()}${serializeStyle()}>${serializeContent()}${serializeChildren()}</${type.serialName}>"
        } else {
            "<${type.serialName}${serializeId()}${serializeClasses()}${serializeStyle()}>"
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
        this._children.add(child)
        child.parent = this
    }

    @HtmlDsl
    inline fun div(body: HtmlElement.() -> Unit) {
        addChild(ru.maeasoftworks.normativecontrol.core.rendering.div(body))
    }

    @HtmlDsl
    inline fun p(body: HtmlElement.() -> Unit) {
        addChild(ru.maeasoftworks.normativecontrol.core.rendering.p(body))
    }

    @HtmlDsl
    inline fun span(body: HtmlElement.() -> Unit) {
        addChild(ru.maeasoftworks.normativecontrol.core.rendering.span(body))
    }

    @HtmlDsl
    inline fun head(body: HtmlElement.() -> Unit) {
        addChild(ru.maeasoftworks.normativecontrol.core.rendering.head(body))
    }

    @HtmlDsl
    inline fun body(body: HtmlElement.() -> Unit) {
        addChild(ru.maeasoftworks.normativecontrol.core.rendering.body(body))
    }

    @HtmlDsl
    inline fun style(body: HtmlElement.() -> Unit) {
        addChild(ru.maeasoftworks.normativecontrol.core.rendering.style(body))
    }

    @HtmlDsl
    inline fun script(body: HtmlElement.() -> Unit) {
        addChild(ru.maeasoftworks.normativecontrol.core.rendering.script(body))
    }

    enum class Type(val serialName: String) {
        DIV("div"),
        P("p"),
        BR("br"),
        SPAN("span"),
        HEAD("head"),
        BODY("body"),
        HTML("html"),
        STYLE("style"),
        SCRIPT("script")
    }
}

@DslMarker
annotation class HtmlDsl

@HtmlDsl
inline fun css(body: Stylesheet.Builder.() -> Unit): Stylesheet {
    return Stylesheet.Builder().apply(body).build()
}

context(VerificationContext)
@HtmlDsl
inline fun div(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.DIV).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun p(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.P).also(body)
}

context(VerificationContext)
@HtmlDsl
fun br(): HtmlElement {
    return HtmlElement(HtmlElement.Type.BR, false)
}

context(VerificationContext)
@HtmlDsl
inline fun span(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.SPAN).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun head(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.HEAD).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun body(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.BODY).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun html(body: HtmlElement.() -> Unit): HtmlElement {
    return object : HtmlElement(Type.HTML) {
        override fun toString(): String {
            return "<!doctype html>" + super.toString()
        }
    }.also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun style(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.STYLE).also(body)
}

context(VerificationContext)
@HtmlDsl
inline fun script(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.SCRIPT).also(body)
}