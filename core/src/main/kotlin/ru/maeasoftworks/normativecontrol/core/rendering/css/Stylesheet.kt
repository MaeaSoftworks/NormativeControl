package ru.maeasoftworks.normativecontrol.core.rendering.css

import java.io.Serializable

class Stylesheet : Serializable {
    val styles: MutableMap<String, Style> = mutableMapOf()

    override fun toString(): String {
        return styles.toList().joinToString("") { "${it.first}{${it.second}}" }
    }

    class Builder {
        private val stylesheet = Stylesheet()

        operator fun String.invoke(builder: context(Style, StyleProperties) StyleBuilder.() -> Unit) {
            val style = Style(noInline = true)
            builder(style, StyleProperties, StyleBuilder)
            stylesheet.styles[this] = style
        }

        fun build() = stylesheet
    }
}