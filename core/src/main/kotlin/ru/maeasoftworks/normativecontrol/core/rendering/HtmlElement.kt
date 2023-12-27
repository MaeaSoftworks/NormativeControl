@file:OptIn(Internal::class)

package ru.maeasoftworks.normativecontrol.core.rendering

import ru.maeasoftworks.normativecontrol.core.annotations.Internal
import ru.maeasoftworks.normativecontrol.core.rendering.css.Style
import ru.maeasoftworks.normativecontrol.core.rendering.css.Stylesheet
import java.io.Serializable

open class HtmlElement @Internal constructor(
    private val type: String,
    private val hasClosingTag: Boolean = true
) {
    val classes: MutableList<String> = mutableListOf()
    var id: String? = null
    var content: Serializable? = null
    val children: MutableList<HtmlElement> = mutableListOf()
    var style: Style = Style()

    private val classesString: String
        get() = if (classes.size > 0) " class='${classes.joinToString(" ")}'" else ""

    private val idString: String
        get() = if (id != null) " id='$id'" else ""

    private val childrenString: String
        get() = if (children.size > 0) children.joinToString("") { it.toString() } else ""

    private val styleString: String
        get() = if (style.size > 0) style.toString().let { if (it != "") " style='$it'" else "" } else ""

    private val contentString: String
        get() = content?.toString() ?: ""

    override fun toString(): String {
        return if (hasClosingTag) {
            "<$type$idString$classesString$styleString>$contentString$childrenString</$type>"
        } else {
            "<$type$idString$classesString$styleString>"
        }
    }

    fun withClass(classname: String): HtmlElement {
        classes.add(classname)
        return this
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
    inline operator fun String.invoke(body: HtmlElement.() -> Unit) {
        children += HtmlElement(this).also(body)
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
}

@DslMarker
annotation class HtmlDsl

@HtmlDsl
inline fun css(body: Stylesheet.Builder.() -> Unit): Stylesheet {
    return Stylesheet.Builder().apply(body).build()
}

@HtmlDsl
inline fun div(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement("div").also(body)
}

@HtmlDsl
inline fun p(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement("p").also(body)
}

@HtmlDsl
fun br(): HtmlElement {
    return HtmlElement("br", false)
}

@HtmlDsl
inline fun span(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement("span").also(body)
}

@HtmlDsl
inline fun head(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement("head").also(body)
}

@HtmlDsl
inline fun body(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement("body").also(body)
}

@HtmlDsl
inline fun html(body: HtmlElement.() -> Unit): HtmlElement {
    return object : HtmlElement("html") {
        override fun toString(): String {
            return "<!doctype html>" + super.toString()
        }
    }.also(body)
}

@HtmlDsl
inline fun style(body: HtmlElement.() -> Unit): HtmlElement {
    return HtmlElement("style").also(body)
}