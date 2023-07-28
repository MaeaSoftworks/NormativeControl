package com.maeasoftworks.rendering.model.css.properties

import java.util.*

/**
 * Base class that is representing CSS property
 * @param measure measure of property value (added in the end of line)
 * @param coefficient if property value is [Number] it will be divided by this value
 * @param converter function that will be applied to value after diving by coefficient
 */
open class Property(
    val measure: String? = null,
    val coefficient: Number? = null,
    val converter: ((Any?) -> String?)? = { it.toString() }
) {
    private val regex = Regex("([a-z0-9](?=[A-Z]))([A-Z])")

    /**
     * Property name default serializer
     * @return property class name in kebab-case
     */
    override fun toString() = this::class.simpleName!!.replace(regex, "$1-$2").lowercase(Locale.getDefault())
}
