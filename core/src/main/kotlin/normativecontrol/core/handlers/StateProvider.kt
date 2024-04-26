package normativecontrol.core.handlers

import normativecontrol.core.AbstractConfiguration
import normativecontrol.core.contexts.VerificationContext

/**
 * Casts [AbstractConfiguration.state] to provided type [S] using [VerificationContext].
 * @param S type of state object
 */
interface StateProvider<S> {
    /**
     * [AbstractConfiguration.state] of [S] type from [VerificationContext].
     */
    context(VerificationContext)
    val state: S
        @Suppress("UNCHECKED_CAST")
        get() = configuration.state as S
}