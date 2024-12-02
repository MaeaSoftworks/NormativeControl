package normativecontrol.core.math

/**
 * [Double] wrapper for easy cm math.
 * @param value value in cm
 */
@JvmInline
value class Centimetre(val value: Double) {
    operator fun times(another: Centimetre): Centimetre = apply(this, another, Double::times)

    operator fun times(another: Int): Centimetre = Centimetre(value * another)

    operator fun plus(another: Centimetre): Centimetre = apply(this, another, Double::plus)

    operator fun minus(another: Centimetre): Centimetre = apply(this, another, Double::minus)

    operator fun div(another: Centimetre): Centimetre = apply(this, another, Double::div)

    operator fun div(another: Int): Centimetre = Centimetre(value / another)

    operator fun compareTo(another: Centimetre) = value.compareTo(another.value)

    private inline fun apply(first: Centimetre, second: Centimetre, operation: Double.(Double) -> Double): Centimetre {
        return Centimetre(first.value.operation(second.value))
    }

    fun round(num: Int): Centimetre {
        var k = 10.0
        repeat(num) { k *= 10 }
        return Centimetre(kotlin.math.round(value * k) / k)
    }
}