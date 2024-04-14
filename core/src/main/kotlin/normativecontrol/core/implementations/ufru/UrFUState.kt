package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.states.State

class UrFUState : State() {
    var rSinceBr: Int = 0
    val referencesInText = mutableSetOf<Int>()
}