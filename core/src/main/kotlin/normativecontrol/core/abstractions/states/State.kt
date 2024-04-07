package normativecontrol.core.abstractions.states

import normativecontrol.core.abstractions.Findable

interface State: Findable {
    override val key: StateFactory

    fun reset() { }
}