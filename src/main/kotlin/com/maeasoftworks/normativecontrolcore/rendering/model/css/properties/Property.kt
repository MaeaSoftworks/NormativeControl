package com.maeasoftworks.normativecontrolcore.rendering.model.css.properties

import java.util.*

/**
 * Base class that is representing CSS property
 * @param measure measure of property value (added in the end of line)
 * @param coefficient if property value is [Number] it will be divided by this value
 * @param converter function that will be applied to value after diving by coefficient
 */
open class Property<T>(
    val measure: String? = null,
    val converter: (T?) -> String? = { it.toString() }
) {
    private val regex = Regex("([a-z0-9](?=[A-Z]))([A-Z])")

    /**
     * Property name default serializer
     * @return property class name in kebab-case
     */
    override fun toString() = this::class.simpleName!!.replace(regex, "$1-$2").lowercase(Locale.getDefault())
}

open class DoubleProperty(
    measure: String? = null,
    coefficient: Double = 1.0,
    converter: (Double?) -> String? = { it.toString() }
) : Property<Double>(
    measure, { converter(it?.div(coefficient)) }
)

open class IntProperty(
    measure: String? = null,
    coefficient: Int = 1,
    converter: (Int?) -> String? = { it.toString() }
) : Property<Int>(
    measure, { converter(it?.div(coefficient)) }
)