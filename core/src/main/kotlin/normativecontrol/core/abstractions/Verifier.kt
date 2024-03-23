package normativecontrol.core.abstractions

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.shared.InterruptableContext
import normativecontrol.shared.interruptable

typealias Verifier<T> = context(VerificationContext, InterruptableContext) (T) -> Unit

fun <T> verifier(verification: Verifier<T>): Verifier<T> {
    return verification
}

context(VerificationContext)
infix fun <T> T.verifyBy(verifier: Verifier<T>): T? {
    if (!chapter.shouldBeVerified) return this
    interruptable {
        verifier(this@VerificationContext, InterruptableContext, this)
    }
    return this
}