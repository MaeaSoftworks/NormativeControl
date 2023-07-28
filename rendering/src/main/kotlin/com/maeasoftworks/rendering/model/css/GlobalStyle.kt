package com.maeasoftworks.rendering.model.css

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

    /**
     * Syntax feature to add styles in global style
     * @param function function that creates styles
     * @sample com.maeasoftworks.rendering.model.html.HTMLFile.createDefaultStyles
     */
    operator fun plusAssign(function: GlobalStyle.() -> Unit) {
        function(this)
    }

    /**
     * Feature to add styles in global style with syntax like
     * ```
     * "*" += {
     *     BoxShadow set "inset 0px 0px 0px 0.5px red"
     *     BoxSizing set "border-box"
     *     Margin set 0
     *     Padding set 0
     * }
     * ```
     */
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
