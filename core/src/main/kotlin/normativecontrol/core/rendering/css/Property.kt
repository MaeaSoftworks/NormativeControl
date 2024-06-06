package normativecontrol.core.rendering.css

import normativecontrol.core.contexts.RenderingContext
import java.math.BigInteger

abstract class Property<T>(val name: String, val converter: (T?) -> String?, private val measure: String?) {
    constructor(name: String, measure: String? = null) : this(name, { it?.toString() }, measure)
    constructor(name: String, converter: (T?) -> String?) : this(name, converter, null)
    constructor(name: String) : this(name, null)

    context(RenderingContext, Style, StyleBuilder)
    open infix fun set(value: T?) {
        if (value != null) {
            val v = this.converter(value)
            if (v != null) {
                addRule(Rule(name, v, measure))
            }
        }
    }
}

context(RenderingContext, Style, StyleBuilder)
infix fun String.set(value: String) {
    addRule(Rule(this, value))
}

open class DoubleProperty(name: String, converter: (Double?) -> String?, measure: String?, coefficient: Double) :
    Property<Double>(name, { converter(it?.div(coefficient)) }, measure) {

    constructor(name: String, converter: (Double?) -> String?, measure: String?) : this(name, converter, measure, 1.0)
    constructor(name: String, measure: String?, coefficient: Double) : this(name, { it?.toString() }, measure, coefficient)
    constructor(name: String, coefficient: Double) : this(name, null, coefficient)
    constructor(name: String, converter: (Double?) -> String?) : this(name, converter, null)
    constructor(name: String) : this(name, { it?.toString() })
}

open class BigIntegerProperty(name: String, converter: (Double?) -> String?, measure: String?, coefficient: Double? = null) : Property<BigInteger>(
    name,
    { if (coefficient == null) converter(it?.toDouble()) else converter(it?.toDouble()?.div(coefficient)) },
    measure
) {
    constructor(name: String, converter: (Double?) -> String?, measure: String?) : this(name, converter, measure, 1.0)
    constructor(name: String, measure: String?, coefficient: Double) : this(name, { it?.toString() }, measure, coefficient)
    constructor(name: String, coefficient: Double) : this(name, null, coefficient)
    constructor(name: String, converter: (Double?) -> String?) : this(name, converter, null)
    constructor(name: String) : this(name, { it?.toString() })
}