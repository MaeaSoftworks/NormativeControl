package ru.maeasoftworks.normativecontrol.core.rendering.css

import java.io.Serializable

class Stylesheet : Serializable {
    private val styles: HashMap<String, Style> = hashMapOf()

    override fun toString(): String {
        return styles.toList().joinToString("") { "${it.first}{${it.second}}" }
    }

    class Builder {
        private val stylesheet = Stylesheet()

        operator fun String.invoke(block: Style.Block.() -> Unit) {
            val s = Style.Block()
            s.block()
            stylesheet.styles[this] = Style().also { it.rules = s.rules }
        }

        fun build() = stylesheet
    }
}