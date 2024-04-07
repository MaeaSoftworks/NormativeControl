package normativecontrol.core.implementations.ufru

import normativecontrol.core.abstractions.states.RunState
import normativecontrol.core.abstractions.states.StateFactory
import normativecontrol.core.rendering.css.Stylesheet

class UrFURunState : RunState {
    var rSinceBr: Int = 0
    var pageStyleId: Int = 0
    var externalGlobalStylesheet = Stylesheet()
    val referencesInText = mutableSetOf<Int>()

    fun foldStylesheet(target: Stylesheet) {
        target.fold(externalGlobalStylesheet)
        externalGlobalStylesheet = Stylesheet()
    }

    override val key: StateFactory = Companion

    companion object: StateFactory {
        override fun createState() = UrFURunState()
    }
}