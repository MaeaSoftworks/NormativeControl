package normativecontrol.core.abstractions

import normativecontrol.core.contexts.VerificationContext

typealias Verifier<T> = context(VerificationContext) (T) -> Unit

fun <T> verifier(verification: Verifier<T>): Verifier<T> {
    return verification
}

context(VerificationContext)
infix fun <T> T.verifyBy(verifier: Verifier<T>): T {
    if (!chapter.shouldBeVerified) return this
    verifier(this@VerificationContext, this)
    return this
}