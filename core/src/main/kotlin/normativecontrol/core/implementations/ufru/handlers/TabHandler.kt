package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.handlers.Factory
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.rendering.html.create
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import org.docx4j.wml.R.Tab

internal class TabHandler : Handler<Tab>() {
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

    @HandlerFactory(Tab::class, UrFUConfiguration::class)
    companion object: Factory<TabHandler> {
        override fun create() = TabHandler()
    }
}