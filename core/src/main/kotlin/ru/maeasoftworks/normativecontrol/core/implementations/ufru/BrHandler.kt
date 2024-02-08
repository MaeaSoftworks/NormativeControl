package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.wml.Br
import org.docx4j.wml.STBrType
import ru.maeasoftworks.normativecontrol.core.abstractions.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.Mapping
import ru.maeasoftworks.normativecontrol.core.abstractions.Profile
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

@EagerInitialization
object BrHandler: Handler<Br>(Profile.UrFU, Mapping.of { BrHandler }) {
    context(VerificationContext)
    override fun handle(element: Any) {
        element as Br
        if (element.type == STBrType.PAGE) {
            render.rSinceBr = 0
            render.pageBreak(1)
        }
    }
}