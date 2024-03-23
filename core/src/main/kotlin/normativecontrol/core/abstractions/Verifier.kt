package normativecontrol.core.abstractions

import normativecontrol.core.contexts.VerificationContext

@JvmInline
value class Verifier<T>(val verification: context(VerificationContext) (T) -> Unit)

fun <T> verifier(verification: context(VerificationContext) (T) -> Unit): Verifier<T> {
    return Verifier(verification)
}

context(VerificationContext)
infix fun <T> T.verifyBy(verifier: Verifier<T>): T? {
    if (!chapter.shouldBeVerified) return this
    verifier.verification(this@VerificationContext, this)
    return this
}