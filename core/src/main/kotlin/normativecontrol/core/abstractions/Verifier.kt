package normativecontrol.core.abstractions

import normativecontrol.core.contexts.VerificationContext

internal typealias Verifier<T> = context(VerificationContext) (T) -> Unit

internal fun <T> verifier(verification: Verifier<T>): Verifier<T> {
    return verification
}

context(VerificationContext)
internal infix fun <T> T.verifyBy(verifier: Verifier<T>): T {
    if (!chapter.shouldBeVerified) return this
    verifier(this@VerificationContext, this)
    return this
}