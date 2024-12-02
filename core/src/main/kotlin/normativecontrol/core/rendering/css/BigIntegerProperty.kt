package normativecontrol.core.rendering.css

import java.math.BigInteger

open class BigIntegerProperty
internal constructor(name: String, measure: String? = null, private val coefficient: Double = 1.0) : Property<BigInteger>(name, measure) {
    override fun converter(value: BigInteger?): String? {
        return value?.toDouble()?.div(coefficient).toString()
    }
}