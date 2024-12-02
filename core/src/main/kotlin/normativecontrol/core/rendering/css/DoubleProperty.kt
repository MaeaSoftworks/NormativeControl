package normativecontrol.core.rendering.css

open class DoubleProperty
internal constructor(name: String, measure: String? = null, private val coefficient: Double = 1.0) : Property<Double>(name, measure) {
    override fun converter(value: Double?): String? {
        return value?.div(coefficient).toString()
    }
}