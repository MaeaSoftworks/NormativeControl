package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.annotations.ReflectHandler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.rendering.html.create
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import org.docx4j.wml.R.Tab

@ReflectHandler(Tab::class, UrFUConfiguration::class)
object TabHandler : Handler<Tab> {
    context(VerificationContext)
    override fun handle(element: Tab) {
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