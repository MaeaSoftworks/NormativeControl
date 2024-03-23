package normativecontrol.shared

object Interruptable

context(Interruptable)
class Interruption private constructor() : Exception() {
    companion object {
        fun interrupt(): Nothing {
            throw with(Interruptable) { Interruption() }
        }
    }
}

@JvmInline
value class Interrupter(val call: () -> Nothing)

inline fun interrupter(crossinline fn: () -> Unit): Interrupter {
    return Interrupter {
        fn()
        Interruption.interrupt()
    }
}

inline fun <T> interruptable(fn: context(Interruptable) () -> T) {
    try {
        fn(Interruptable)
    } catch (e: Interruption) {
        return
    }
}

context(Interruptable)
fun <T> T?.interruptIfNullWith(interrupter: Interrupter): T {
    return this ?: run { interrupter.call() }
}

context(Interruptable)
fun <T> T?.interruptIfNullWith(lazyInterrupter: () -> Interrupter): T {
    return this ?: run { lazyInterrupter().call() }
}

context(Interruptable)
inline fun <T> interruptOnAnyException(lazyInterrupter: () -> Interrupter, body: () -> T): T {
    return try {
        body()
    } catch (e: Exception) {
        lazyInterrupter().call()
    }
}

context(Interruptable)
inline fun <T> interruptOnAnyException(interrupter: Interrupter, body: () -> T): T {
    return try {
        body()
    } catch (e: Exception) {
        interrupter.call()
    }
}
