package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.states.AbstractGlobalState
import normativecontrol.core.css.Stylesheet

class GlobalState : AbstractGlobalState() {
    var rSinceBr: Int = 0
    var pageStyleId: Int = 0
    var externalGlobalStylesheet = Stylesheet()
    val referencesInText = mutableSetOf<Int>()

    fun foldStylesheet(target: Stylesheet) {
        target.fold(externalGlobalStylesheet)
        externalGlobalStylesheet = Stylesheet()
    }
}