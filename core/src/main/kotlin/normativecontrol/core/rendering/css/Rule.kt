package normativecontrol.core.rendering.css

/**
 * Representation of CSS rule, e.g.:
 * ```css
 * display: block;
 * ```
 * @property property CSS property
 * @property value Property value set in the current rule
 * @property measure Unit of measurement of value, null if not necessary
 */
data class Rule(val property: String, var value: String?, val measure: String? = null) {
    /**
     * Serializes object to CSS format string.
     * @return string of CSS code. If [value] of rule was null returns null.
     */
    fun serialize() = if (value != null) "$property:$value${measure ?: ""}" else null
}