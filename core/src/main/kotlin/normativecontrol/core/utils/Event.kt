package normativecontrol.core.utils

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.Handler

class Event<H : Handler<T>, T> {
    private val callbacks = mutableSetOf<H.(ctx: VerificationContext) -> Unit>()

    fun add(callback: H.(ctx: VerificationContext) -> Unit) {
        callbacks += callback
    }

    operator fun invoke(handler: H, context: VerificationContext) {
        callbacks.forEach {
            it(handler, context)
        }
    }
}