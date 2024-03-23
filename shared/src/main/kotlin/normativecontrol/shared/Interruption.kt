package normativecontrol.shared

/**
 * Context for all actions that can be interrupted.
 */
object InterruptableContext

/**
 * Function that can be interrupted by [Interrupter] or [Interruption.interrupt].
 */
typealias Interruptable = context(InterruptableContext) () -> Unit

/**
 * Exception class for interruption. Should be used only in [InterruptableContext].
 */
context(InterruptableContext)
class Interruption private constructor() : Exception() {
    companion object {

        /**
         * Throws new [Interruption]. Can be called only from [InterruptableContext].
         */
        fun interrupt(): Nothing {
            throw with(InterruptableContext) { Interruption() }
        }
    }
}

/**
 * Function that should interrupt [Interruptable].
 */
typealias Interrupter = () -> Nothing

/**
 * Creates a new [Interrupter].
 * @param action will be executed before interruption.
 * @return new [Interrupter] function.
 */
inline fun interrupter(crossinline action: () -> Unit): Interrupter {
    return {
        action()
        Interruption.interrupt()
    }
}

/**
 * Starts [interruptable] function and stops when it was interrupted.
 * @param interruptable function that can be interrupted by [Interrupter].
 */
inline fun interruptable(interruptable: Interruptable) {
    try {
        interruptable(InterruptableContext)
    } catch (e: Interruption) {
        return
    }
}

context(InterruptableContext)
inline fun <T> T?.interruptIfNullWith(lazyInterrupter: () -> Interrupter): T {
    return this ?: run { lazyInterrupter().invoke() }
}

context(InterruptableContext)
inline fun <T> interruptOnAnyException(lazyInterrupter: () -> Interrupter, body: () -> T): T {
    return try {
        body()
    } catch (e: Exception) {
        lazyInterrupter().invoke()
    }
}
