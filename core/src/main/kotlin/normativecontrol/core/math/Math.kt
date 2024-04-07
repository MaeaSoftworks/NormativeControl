package normativecontrol.core.math

import normativecontrol.core.rendering.html.Constants
import java.math.BigInteger

fun BigInteger.asTwip(): Twip = Twip(this)

fun BigInteger.asPointsToLine(): Double {
    return this.toDouble() / Constants.POINTS_IN_LINES
}

val Twip.inches: Inch
    get() = Inch(this.value.toDouble() / Constants.TWENTIETHS_OF_POINT_IN_INCH)

val Inch.cm: Centimetre
    get() = Centimetre(this.value * Constants.CENTIMETRES_IN_INCH)

val Double.cm: Centimetre
    get() = Centimetre(this)

val Twip.cm: Centimetre
    get() = this.inches.cm

fun abs(a: Centimetre) = Centimetre(kotlin.math.abs(a.double))