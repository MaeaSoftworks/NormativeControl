package normativecontrol.core.abstractions.states

import normativecontrol.core.contexts.VerificationContext

interface Stateful<S: State> {
    val stateFactory: StateFactory

    context(VerificationContext)
    val state: S
}