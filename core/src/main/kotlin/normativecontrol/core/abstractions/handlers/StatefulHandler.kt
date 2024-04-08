package normativecontrol.core.abstractions.handlers

import normativecontrol.core.abstractions.states.State
import normativecontrol.core.abstractions.states.Stateful
import normativecontrol.core.contexts.VerificationContext

interface StatefulHandler<T, S: State>: Handler<T>, Stateful<S> {
    context(VerificationContext)
    @Suppress("UNCHECKED_CAST")
    override val state: S
        get() = (__states[stateFactory] ?: stateFactory.createState().also { __states[stateFactory] = it }) as S
}