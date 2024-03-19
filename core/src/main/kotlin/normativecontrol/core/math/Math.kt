package normativecontrol.core.math

import normativecontrol.core.html.Constants
import java.math.BigInteger

@JvmInline
value class Twip(val value: BigInteger)

fun BigInteger.asTwip(): Twip = Twip(this)

fun BigInteger.asPointsToLine(): Double {
    return this.toDouble() / Constants.POINTS_IN_LINES
}

@JvmInline
value class Inch(val value: Double)

val Twip.inches: Inch
    get() = Inch(this.value.toDouble() / Constants.TWENTIETHS_OF_POINT_IN_INCH)

@JvmInline
value class Centimetre(val double: Double) {
    operator fun times(another: Centimetre): Centimetre = operator(this, another, Double::times)

    operator fun plus(another: Centimetre): Centimetre = operator(this, another, Double::plus)

    operator fun minus(another: Centimetre): Centimetre = operator(this, another, Double::minus)

    operator fun div(another: Centimetre): Centimetre = operator(this, another, Double::div)

    operator fun compareTo(another: Centimetre) = double.compareTo(another.double)

    private inline fun operator(first: Centimetre, second: Centimetre, operation: Double.(Double) -> Double): Centimetre {
        return Centimetre(first.double.operation(second.double))
    }
}

val Inch.cm: Centimetre
    get() = Centimetre(this.value * Constants.CENTIMETRES_IN_INCH)

val Double.cm: Centimetre
    get() = Centimetre(this)

val Twip.cm: Centimetre
    get() = this.inches.cm

fun abs(a: Centimetre) = Centimetre(kotlin.math.abs(a.double))