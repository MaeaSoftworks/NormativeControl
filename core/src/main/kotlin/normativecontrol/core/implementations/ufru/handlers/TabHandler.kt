package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.html.create
import org.docx4j.wml.R.Tab

object TabHandler : Handler<Tab, Nothing, Nothing>() {
    context(VerificationContext)
    override fun handle(element: Any) {
        render.pointer!!.addChild(create("tab") {
            classes += "t"
            content = "&emsp;"
            style += {
                width set doc.documentSettingsPart.jaxbElement.defaultTabStop.`val`.toDouble()
                "display" set "inline-block"
            }
        })
    }
}