package normativecontrol.implementation.urfu

import normativecontrol.core.states.State

class UrFUState : State() {
    var rSinceBr: Int = 0
    val referencesInText = mutableSetOf<Int>()

    var sinceDrawing: Int = -1
    var currentPWithDrawing = false
}