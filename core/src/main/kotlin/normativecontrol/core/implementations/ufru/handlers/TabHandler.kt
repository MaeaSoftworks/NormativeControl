package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.AbstractHandler
import normativecontrol.core.annotations.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.html.create
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import org.docx4j.wml.R.Tab

@Handler(Tab::class, UrFUConfiguration::class)
object TabHandler : AbstractHandler() {
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