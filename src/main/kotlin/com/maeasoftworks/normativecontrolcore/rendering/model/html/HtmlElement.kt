package com.maeasoftworks.normativecontrolcore.rendering.model.html

import com.maeasoftworks.normativecontrolcore.rendering.model.css.Style

class HtmlElement(
    val type: String,
    private val hasClosingTag: Boolean = true
) {
    private var classes: MutableList<String> = mutableListOf()
    var id: String? = null
    val content: StringBuilder = StringBuilder()
    val children: MutableList<HtmlElement> = ArrayList()
    var style: Style = Style()

    private val classesString: String
        get() = if (classes.size > 0) " class='${classes.joinToString(" ")}'" else ""

    private val idString: String
        get() = if (id != null) " id='$id'" else ""

    private val childrenString: String
        get() = if (children.size > 0) children.joinToString("") { it.toString() } else ""

    private val styleString: String
        get() = if (style.size > 0) style.toString().let { if (it != "") " style='$it'" else "" } else ""

    override fun toString(): String {
        return if (hasClosingTag) {
            "<$type$idString$classesString$styleString>$content$childrenString</$type>"
        } else {
            "<$type$idString$classesString$styleString>"
        }
    }

    fun style(builder: Style.Builder.() -> Unit) {
        val s = Style.Builder()
        s.builder()
        style = s.build()
    }

    fun withClass(classname: String): HtmlElement {
        classes.add(classname)
        return this
    }

    fun duplicate(): HtmlElement {
        return HtmlElement(this@HtmlElement.type).also {
            it.classes = classes
            it.id = id
            it.style = style
        }
    }
}
