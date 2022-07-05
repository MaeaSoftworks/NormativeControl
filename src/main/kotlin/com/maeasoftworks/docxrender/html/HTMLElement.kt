package com.maeasoftworks.docxrender.html

class HTMLElement(
    val type: String
) {
    private val classes: MutableList<String> = mutableListOf()
    var id: String? = null
    val content: StringBuilder = StringBuilder()
    val children: MutableList<HTMLElement> = ArrayList()
    private val elementStyle: ElementStyle = ElementStyle()

    override fun toString(): String {
        return "<$type${idToString()}${classesToString()}$elementStyle>$content${childrenToString()}</$type>"
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
            children.joinToString(" ") { it.toString() }
        } else {
            ""
        }
    }

    fun duplicate(): HTMLElement {
        return HTMLElement(this@HTMLElement.type).apply {
            this.classes.addAll(this@HTMLElement.classes)
            this.id = this@HTMLElement.id.toString()
        }
    }
}