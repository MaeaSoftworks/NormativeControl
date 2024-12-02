package normativecontrol.core

import normativecontrol.core.contexts.VerificationContext
import org.jetbrains.annotations.Contract

/**
 * Function that verifies element property.
 * @param T type of verified property
 */
typealias Verifier<T> = context(VerificationContext) (T) -> Unit

/**
 * Wrapper function to verifier functions creation without type casts.
 * @param T type of verified property
 * @param verification verifier body
 * @return verifier
 */
fun <T> verifier(verification: Verifier<T>): Verifier<T> {
    return verification
}

/**
 * Call verifier on receiver.
 * @param T type of verified property
 * @param verifier verifier function
 * @receiver property that need to be verified
 * @return verified property
 */
context(VerificationContext)
@Contract(pure = false)
infix fun <T> T.verifyBy(verifier: Verifier<T>): T {
    if (!chapter.shouldBeVerified) return this
    verifier(this@VerificationContext, this)
    return this
}