package normativecontrol.implementation.urfu

import normativecontrol.core.states.State
import normativecontrol.core.utils.IntCounter

class UrFUState : State() {
    var isHeader = false

    var isCodeBlock = false
    var sinceCodeBlock = 0

    var rSinceBr: Int = 0
    val referencesInText = mutableSetOf<Int>()

    var sinceDrawing: Int = -1
    var currentPWithDrawing = false

    val tableTitleCounter = IntCounter()

    val tableCounter = IntCounter()
}