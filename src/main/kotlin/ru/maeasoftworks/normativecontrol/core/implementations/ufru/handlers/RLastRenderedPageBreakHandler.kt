package ru.maeasoftworks.normativecontrol.core.implementations.ufru.handlers

import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.HandlerConfig
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile.globalState

@EagerInitialization
object RLastRenderedPageBreakHandler : Handler<R.LastRenderedPageBreak, Nothing>(
    HandlerConfig.create {
        setHandler { RLastRenderedPageBreakHandler }
        setTarget<R.LastRenderedPageBreak>()
        setProfile(UrFUProfile)
    }
) {
    context(VerificationContext)
    override fun handle(element: Any) {
        if (globalState.rSinceBr > 2)
            render.pageBreak(1)
    }
}