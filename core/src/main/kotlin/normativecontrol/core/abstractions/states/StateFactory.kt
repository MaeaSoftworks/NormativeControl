package normativecontrol.core.abstractions.states

import normativecontrol.core.abstractions.Key

interface StateFactory : Key {
    fun createState(): State
}