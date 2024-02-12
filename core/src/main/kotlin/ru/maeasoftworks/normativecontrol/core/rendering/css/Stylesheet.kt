package ru.maeasoftworks.normativecontrol.core.rendering.css

import java.io.Serializable

class Stylesheet : Serializable {
    val styles: MutableMap<String, Style> = mutableMapOf()

    override fun toString(): String {
        return styles.toList().joinToString("") { "${it.first}{${it.second}}" }
    }

    class Builder {
        private val stylesheet = Stylesheet()

        operator fun String.invoke(block: Style.Block.() -> Unit) {
            val s = Style.Block(true)
            s.block()
            stylesheet.styles[this] = Style().also { it.rules.addAll(s.rules) }
        }

        fun build() = stylesheet
    }
}