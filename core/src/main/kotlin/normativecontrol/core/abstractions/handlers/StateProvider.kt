package normativecontrol.core.abstractions.handlers

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.abstractions.Configuration

/**
 * Casts [Configuration.state] to provided type [S] using [VerificationContext].
 * @param S type of state object
 */
internal interface StateProvider<S> {
    /**
     * [Configuration.state] of [S] type from [VerificationContext].
     */
    context(VerificationContext)
    val state: S
        @Suppress("UNCHECKED_CAST")
        get() = configuration.state as S
}