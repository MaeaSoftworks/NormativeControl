package normativecontrol.core.states

import normativecontrol.core.rendering.css.Stylesheet

abstract class State {
    var externalGlobalStylesheet = Stylesheet()
    var pageStyleId: Int = 0

    fun foldStylesheet(target: Stylesheet) {
        target.fold(externalGlobalStylesheet)
        externalGlobalStylesheet = Stylesheet()
    }
}