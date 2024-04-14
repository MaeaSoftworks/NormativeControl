package normativecontrol.core.abstractions.handlers

import normativecontrol.core.contexts.VerificationContext

interface StateProvider<S> {
    context(VerificationContext)
    @Suppress("UNCHECKED_CAST")
    val state: S
        get() = configuration.state as S
}