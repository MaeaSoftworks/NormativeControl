package normativecontrol.core.abstractions.states

import normativecontrol.core.css.Stylesheet

class RenderingState {
    var pageStyleId: Int = 0
    var externalGlobalStylesheet = Stylesheet()
    val referencesInText = mutableSetOf<Int>()

    fun foldStylesheet(target: Stylesheet) {
        target.fold(externalGlobalStylesheet)
        externalGlobalStylesheet = Stylesheet()
    }
}