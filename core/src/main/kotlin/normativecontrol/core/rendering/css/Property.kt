package normativecontrol.core.rendering.css

import normativecontrol.core.contexts.RenderingContext

/**
 * Representation of CSS properties.
 * @property name Property name
 * @property measure Unit of measurements for property value
 */
abstract class Property<T> internal constructor(val name: String, private val measure: String? = null) {
    /**
     * Converts value of property to CSS recognizable string.
     * @param value property of value that will be set during document verification
     * @return CSS recognizable string
     */
    open fun converter(value: T?): String? {
        return value.toString()
    }

    /**
     * Sets value to this property.
     * Adds new [Rule] to current [DeclarationBlock] with incoming value.
     * @param value property value
     */
    context(RenderingContext, DeclarationBlock, StyleBuilder)
    @CssDsl
    open infix fun set(value: T?) {
        if (value != null) {
            val v = this.converter(value)
            if (v != null) {
                addRule(Rule(name, v, measure))
            }
        }
    }

    protected fun colorConverter(color: String?): String? {
        if (color == null) return null
        if (color.startsWith('#')) return color
        return try {
            color.toLong(16)
            "#$color"
        } catch (e: NumberFormatException) {
            if (color != "null") return color
            null
        }
    }
}
