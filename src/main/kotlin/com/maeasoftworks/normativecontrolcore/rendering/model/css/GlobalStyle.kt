package com.maeasoftworks.normativecontrolcore.rendering.model.css

/**
 * Representation of CSS styles in HTML `header` tag
 */
class GlobalStyle {
    /**
     * `selector` - `style` map
     */
    private val styles: HashMap<String, Style> = hashMapOf()

    /**
     * Get style by selector
     * @param selector CSS selector
     * @return style
     */
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

    /**
     * Serializes all styles in one string
     * @return serialized styles
     */
    override fun toString(): String {
        return styles.toList().joinToString("") { "${it.first}{${it.second}}" }
    }
}
