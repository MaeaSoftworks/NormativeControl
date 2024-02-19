package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import ru.maeasoftworks.normativecontrol.core.abstractions.states.AbstractGlobalState
import ru.maeasoftworks.normativecontrol.core.css.Stylesheet

class GlobalState : AbstractGlobalState() {
    var rSinceBr: Int = 0
    var pageStyleId: Int = 0
    var externalGlobalStylesheet = Stylesheet()

    fun foldStylesheet(target: Stylesheet) {
        target.fold(externalGlobalStylesheet)
        externalGlobalStylesheet = Stylesheet()
    }
}