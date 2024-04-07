package normativecontrol.core.math

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