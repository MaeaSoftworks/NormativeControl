package ru.maeasoftworks.normativecontrol.core.implementations.ufru.handlers

import org.docx4j.wml.R.Tab
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.Handler
import ru.maeasoftworks.normativecontrol.core.abstractions.handlers.HandlerConfig
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext
import ru.maeasoftworks.normativecontrol.core.html.create
import ru.maeasoftworks.normativecontrol.core.html.span
import ru.maeasoftworks.normativecontrol.core.implementations.ufru.UrFUProfile

@EagerInitialization
object TabHandler: Handler<Tab, Nothing>(
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