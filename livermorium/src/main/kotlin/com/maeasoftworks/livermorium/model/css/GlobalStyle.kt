package com.maeasoftworks.livermorium.model.css

class GlobalStyle {
    private val styles: HashMap<String, Style> = hashMapOf()

    operator fun get(selector: String): Style {
        return styles[selector] ?: throw NullPointerException()
    }

    operator fun plusAssign(function: GlobalStyle.() -> Unit) {
        function(this)
    }

    operator fun String.plusAssign(function: Style.() -> Unit) {
        val s = Style()
        styles[this] = s
        function(s)
    }

    override fun toString(): String {
        return styles.toList().joinToString("") { "${it.first}{${it.second}}" }
    }
}