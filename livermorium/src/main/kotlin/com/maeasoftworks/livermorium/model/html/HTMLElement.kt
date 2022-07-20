package com.maeasoftworks.livermorium.model.html

class HTMLElement(
    val type: String,
    val hasClosingTag: Boolean = true
) {
    private var classes: MutableList<String> = mutableListOf()
    var id: String? = null
    val content: StringBuilder = StringBuilder()
    val children: MutableList<HTMLElement> = ArrayList()
    var style: Style = Style()

    override fun toString(): String {
        return if (hasClosingTag)
            "<$type${idToString()}${classesToString()}${styleToString()}>$content${childrenToString()}</$type>"
        else
            "<$type${idToString()}${classesToString()}${styleToString()}>"
    }

    fun withClass(classname: String): HTMLElement {
        classes.add(classname)
        return this
    }

    private fun classesToString(): String {
        return if (classes.size > 0) {
            " class='${classes.joinToString(" ")}'"
        } else {
            ""
        }
    }

    private fun idToString(): String {
        return if (id != null) {
            " id='$id'"
        } else {
            ""
        }
    }

    private fun childrenToString(): String {
        return if (children.size > 0) {
            children.joinToString("") { it.toString() }
        } else {
            ""
        }
    }

    private fun styleToString(): String {
        return if (style.size > 0) {
            val s = style.toString()
            if (s != "") " style='$s'" else ""
        } else {
            ""
        }
    }

    fun duplicate(): HTMLElement {
        return HTMLElement(this@HTMLElement.type).apply {
            this.classes = this@HTMLElement.classes
            this.id = this@HTMLElement.id
            this.style = this@HTMLElement.style
        }
    }
}