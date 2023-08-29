package com.maeasoftworks.normativecontrolcore.rendering.model.css

class Stylesheet {
    private val styles: HashMap<String, Style> = hashMapOf()

    override fun toString(): String {
        return styles.toList().joinToString("") { "${it.first}{${it.second}}" }
    }

    class Builder {
        private val stylesheet = Stylesheet()

        operator fun String.plusAssign(builder: Style.Builder.() -> Unit) {
            val s = Style.Builder()
            s.builder()
            stylesheet.styles[this] = s.build()
        }

        fun build() = stylesheet
    }
}