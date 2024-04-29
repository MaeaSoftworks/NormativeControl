package normativecontrol.core.utils

import normativecontrol.core.contexts.VerificationContext

class Event<I> {
    private val callbacks = mutableSetOf<context(VerificationContext) (I) -> Unit>()

    fun subscribe(callback: context(VerificationContext) (I) -> Unit) {
        callbacks += callback
    }

    operator fun plusAssign(callback: context(VerificationContext) (I) -> Unit) = subscribe(callback)

    context(VerificationContext)
    operator fun invoke(arg: I) {
        callbacks.forEach {
            it(this@VerificationContext, arg)
        }
    }
}