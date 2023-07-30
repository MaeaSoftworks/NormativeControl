package com.maeasoftworks.rendering.model.css

/**
 * Representation of CSS rule (property name + value + measure)
 * @param property property name
 * @param value property value
 * @param measure property measure
 */
data class Rule(val property: String, var value: String?, val measure: String? = null) {
    /**
     * Rule's serialization
     * @return rule in format `$property:$value$measure`
     */
    fun serialize() = if (value != null) "$property:$value${measure ?: ""}" else null
}
