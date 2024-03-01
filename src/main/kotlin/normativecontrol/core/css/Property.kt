package normativecontrol.core.css

abstract class Property<T>(val name: String, val converter: (T?) -> String?, val measure: String?) {
    constructor(name: String, measure: String? = null) : this(name, { it?.toString() }, measure)
    constructor(name: String, converter: (T?) -> String?) : this(name, converter, null)
    constructor(name: String) : this(name, null)
}

open class DoubleProperty(name: String, converter: (Double?) -> String?, measure: String?, coefficient: Double) :
    Property<Double>(name, { converter(it?.div(coefficient)) }, measure) {

    constructor(name: String, converter: (Double?) -> String?, measure: String?) : this(name, converter, measure, 1.0)
    constructor(name: String, measure: String?, coefficient: Double) : this(name, { it?.toString() }, measure, coefficient)
    constructor(name: String, coefficient: Double) : this(name, null, coefficient)
    constructor(name: String, converter: (Double?) -> String?) : this(name, converter, null)
    constructor(name: String) : this(name, { it?.toString() })
}