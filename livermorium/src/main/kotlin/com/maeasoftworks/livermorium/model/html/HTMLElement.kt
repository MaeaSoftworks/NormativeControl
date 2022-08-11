package com.maeasoftworks.livermorium.model.html

import com.maeasoftworks.livermorium.model.css.Rule
import com.maeasoftworks.livermorium.model.css.Style

/**
 * Basic DOM element
 * @param type element type
 * @param hasClosingTag marks that element need to have closing tag
 */
class HTMLElement(
    val type: String,
    private val hasClosingTag: Boolean = true
) {
    private var classes: MutableList<String> = mutableListOf()
    var id: String? = null
    val content: StringBuilder = StringBuilder()
    val children: MutableList<HTMLElement> = ArrayList()
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
        return if (hasClosingTag)
            "<$type$idString$classesString$styleString>$content$childrenString</$type>"
        else
            "<$type$idString$classesString$styleString>"
    }

    /**
     * Adds class to this element
     *
     * Example usage: `HTMLElement("div").withClass("example-class")`
     * @param classname class that will be added
     * @return element with class from parameter
     */
    fun withClass(classname: String): HTMLElement {
        classes.add(classname)
        return this
    }

    fun duplicate(): HTMLElement {
        //todo: fix ability to edit styles of element of previous page on new page
        return HTMLElement(this@HTMLElement.type).apply {
            this.classes = this@HTMLElement.classes
            this.id = this@HTMLElement.id
            this.style = Style().also { style ->
                for (rule in style.rules) {
                    style.rules.add(Rule(rule.property + "", rule.value + "", rule.measure + ""))
                }
            }
        }
    }
}