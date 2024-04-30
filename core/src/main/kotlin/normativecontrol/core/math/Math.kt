package normativecontrol.core.math

import normativecontrol.core.rendering.html.Constants
import java.math.BigInteger

/**
 * Wraps [BigInteger] to [Twip] object.
 * @receiver value
 * @return value as twips
 */
fun BigInteger.asTwip(): Twip = Twip(this)

/**
 * Expresses receiver as points count and converts it to lines count.
 * @receiver value that will be expresses as points
 * @return lines count in this points count
 */
fun BigInteger.asPointsToLine(): Double {
    return this.toDouble() / Constants.POINTS_IN_LINES
}

/**
 * Returns inches in `this` twip.
 */
val Twip.inches: Inch
    get() = Inch(this.value.toDouble() / Constants.TWENTIETHS_OF_POINT_IN_INCH)

/**
 * Returns centimetres in `this` inches.
 */
val Inch.cm: Centimetre
    get() = Centimetre(this.value * Constants.CENTIMETRES_IN_INCH)

/**
 * Wraps `this` [Double] to [Centimetre].
 */
val Double.cm: Centimetre
    get() = Centimetre(this)

/**
 * Converts `this` [Twip] to [Centimetre].
 */
val Twip.cm: Centimetre
    get() = this.inches.cm

/**
 * Absolute value of [a] centimetres.
 */
fun abs(a: Centimetre) = Centimetre(kotlin.math.abs(a.value))