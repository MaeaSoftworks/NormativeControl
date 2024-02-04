@file:OptIn(Internal::class)

package ru.maeasoftworks.normativecontrol.core.rendering

import ru.maeasoftworks.normativecontrol.core.annotations.Internal
import ru.maeasoftworks.normativecontrol.core.rendering.css.Style
import ru.maeasoftworks.normativecontrol.core.rendering.css.Stylesheet
import java.io.Serializable

open class HtmlElement @Internal constructor(
    val type: Type,
    private val hasClosingTag: Boolean = true,
) {
    val classes: MutableList<String> = mutableListOf()
    var id: String? = null
    var content: Serializable? = null
    val children: MutableList<HtmlElement> = mutableListOf()
    var style: Style = Style()
    var parent: HtmlElement? = null

    private fun serializeClasses(): String = if (classes.size > 0) " class='${classes.joinToString(" ")}'" else ""

    private fun serializeId(): String = if (id != null) " id='$id'" else ""

    private fun serializeChildren(): String = if (children.size > 0) children.joinToString("") { it.toString() } else ""

    private fun serializeStyle(): String = if (style.size > 0) style.toString().let { if (it != "") " style='$it'" else "" } else ""

    private fun serializeContent(): String = content?.toString() ?: ""

    override fun toString(): String {
        return if (hasClosingTag) {
            "<${type.serialName}${serializeId()}${serializeClasses()}${serializeStyle()}>${serializeContent()}${serializeChildren()}</${type.serialName}>"
        } else {
            "<${type.serialName}${serializeId()}${serializeClasses()}${serializeStyle()}>"
        }
    }

    @OptIn(Internal::class)
    fun duplicate(): HtmlElement {
        return HtmlElement(this@HtmlElement.type).also {
            it.classes.addAll(classes)
            it.id = id
            it.style = style
        }
    }

    @HtmlDsl
    inline fun div(body: HtmlElement.() -> Unit) {
        children += ru.maeasoftworks.normativecontrol.core.rendering.div(body)
    }

    @HtmlDsl
    inline fun p(body: HtmlElement.() -> Unit) {
        children += ru.maeasoftworks.normativecontrol.core.rendering.p(body)
    }

    @HtmlDsl
    inline fun span(body: HtmlElement.() -> Unit) {
        children += ru.maeasoftworks.normativecontrol.core.rendering.span(body)
    }

    @HtmlDsl
    inline fun head(body: HtmlElement.() -> Unit) {
        children += ru.maeasoftworks.normativecontrol.core.rendering.head(body)
    }

    @HtmlDsl
    inline fun body(body: HtmlElement.() -> Unit) {
        children += ru.maeasoftworks.normativecontrol.core.rendering.body(body)
    }

    @HtmlDsl
    inline fun style(body: HtmlElement.() -> Unit) {
        children += ru.maeasoftworks.normativecontrol.core.rendering.style(body)
    }

    enum class Type(val serialName: String) {
        DIV("div"),
        P("p"),
        BR("br"),
        SPAN("span"),
        HEAD("head"),
        BODY("body"),
        HTML("html"),
        STYLE("style")
    }
}

@DslMarker
annotation class HtmlDsl

@HtmlDsl
inline fun css(body: Stylesheet.Builder.() -> Unit): Stylesheet {
    return Stylesheet.Builder().apply(body).build()
}

@HtmlDsl
inline fun div(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.DIV).also(body)
}

@HtmlDsl
inline fun p(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.P).also(body)
}

@HtmlDsl
fun br(): HtmlElement {
    return HtmlElement(HtmlElement.Type.BR, false)
}

@HtmlDsl
inline fun span(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.SPAN).also(body)
}

@HtmlDsl
inline fun head(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.HEAD).also(body)
}

@HtmlDsl
inline fun body(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.BODY).also(body)
}

@HtmlDsl
inline fun html(body: HtmlElement.() -> Unit): HtmlElement {
    return object : HtmlElement(Type.HTML) {
        override fun toString(): String {
            return "<!doctype html>" + super.toString()
        }
    }.also(body)
}

@HtmlDsl
inline fun style(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement(HtmlElement.Type.STYLE).also(body)
}