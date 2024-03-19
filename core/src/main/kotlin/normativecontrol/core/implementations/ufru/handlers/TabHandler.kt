package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerConfig
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.html.create
import normativecontrol.core.implementations.ufru.UrFUProfile
import org.docx4j.wml.R.Tab

@EagerInitialization
object TabHandler : Handler<Tab, Nothing>(
    HandlerConfig.create {
        setTarget<Tab>()
        setHandler { TabHandler }
        setProfile(UrFUProfile)
    }
) {
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