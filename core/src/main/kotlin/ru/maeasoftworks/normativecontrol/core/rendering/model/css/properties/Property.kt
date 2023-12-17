package ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties

abstract class Property<T>(
    val name: String,
    val converter: (T?) -> String? = { it?.toString() },
    val measure: String? = null
) {
    var value: T? = null

    inline operator fun invoke(value: () -> T?) {
        this.value = value()
    }
}

open class DoubleProperty(
    name: String,
    coefficient: Double = 1.0,
    measure: String? = null,
    converter: (Double?) -> String? = { it?.toString() }
) : Property<Double>(
    name,
    { converter(it?.div(coefficient)) },
    measure
)

open class IntProperty(
    name: String,
    coefficient: Int = 1,
    measure: String? = null,
    converter: (Int?) -> String? = { it?.toString() }
) : Property<Int>(
    name,
    { converter(it?.div(coefficient)) },
    measure
)