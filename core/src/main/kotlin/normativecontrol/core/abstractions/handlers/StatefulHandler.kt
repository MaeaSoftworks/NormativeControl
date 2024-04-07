package normativecontrol.core.abstractions.handlers

import normativecontrol.core.abstractions.states.State
import normativecontrol.core.abstractions.states.StateFactory
import normativecontrol.core.abstractions.states.Stateful
import normativecontrol.core.contexts.VerificationContext

interface StatefulHandler<T, S: State>: Handler<T>, Stateful<S> {
    override var stateFactory: StateFactory

    context(VerificationContext)
    @Suppress("UNCHECKED_CAST")
    override val state: S
        get() = (states[stateFactory] ?: stateFactory.createState().also { states[stateFactory] = it }) as S
}