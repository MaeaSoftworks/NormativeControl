package ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties

open class Property<T>(
    val name: String,
    val value: T?,
    val converter: (T?) -> String? = { it?.toString() },
    val measure: String? = null
)

open class DoubleProperty(
    name: String,
    value: Double?,
    coefficient: Double = 1.0,
    measure: String? = null,
    converter: (Double?) -> String? = { it?.toString() }
) : Property<Double>(
    name,
    value,
    { converter(it?.div(coefficient)) },
    measure
)

open class IntProperty(
    name: String,
    value: Int?,
    coefficient: Int = 1,
    measure: String? = null,
    converter: (Int?) -> String? = { it?.toString() }
) : Property<Int>(
    name,
    value,
    { converter(it?.div(coefficient)) },
    measure
)
