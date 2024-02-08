package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.Mapping
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

@EagerInitialization
object RLastRenderedPageBreakHandler: Handler<R.LastRenderedPageBreak>(Profile.UrFU, Mapping.of { RLastRenderedPageBreakHandler }) {
    context(VerificationContext)
    override fun handle(element: Any) {
        if (render.rSinceBr > 2)
            render.pageBreak(1)
    }
}