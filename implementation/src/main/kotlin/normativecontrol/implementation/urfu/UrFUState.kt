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

    val sinceLastTableCounter = IntCounter()

    var suppressChapterRecognition = false

    var inSdtBlock = false

    var sinceSdtBlock = -1

    var noSdtBlockReported = false

    var forceLegacyHeaderSearch = false
}