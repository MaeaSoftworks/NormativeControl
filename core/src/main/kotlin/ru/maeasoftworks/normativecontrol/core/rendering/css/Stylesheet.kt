package ru.maeasoftworks.normativecontrol.core.rendering.css

import java.io.Serializable

class Stylesheet : Serializable {
    private val styles: HashMap<String, Style> = hashMapOf()

    override fun toString(): String {
        return styles.toList().joinToString("") { "${it.first}{${it.second}}" }
    }

    class Builder {
        private val stylesheet = Stylesheet()

        operator fun String.invoke(builder: Style.Builder.() -> Unit) {
            val s = Style.Builder()
            s.builder()
            stylesheet.styles[this] = Style().also { it.rules = s.rules }
        }

        fun build() = stylesheet
    }
}