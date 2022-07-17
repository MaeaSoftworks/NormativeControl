package com.maeasoftworks.docxrender.model.html

class GlobalStyle {
    private val styles: HashMap<String, Style> = hashMapOf()

    operator fun get(selector: String): Style {
        return styles[selector] ?: throw NullPointerException()
    }

    operator fun set(selector: String, value: Style.() -> Unit) {
        val s = Style()
        styles[selector] = s
        value(s)
    }

    override fun toString(): String {
        return styles.toList().joinToString("") { "${it.first}{${it.second}}" }
    }
}