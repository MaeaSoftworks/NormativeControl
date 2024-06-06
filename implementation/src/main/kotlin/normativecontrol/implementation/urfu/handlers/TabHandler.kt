package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.handlers.Handler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.rendering.css.set
import normativecontrol.core.rendering.html.create
import normativecontrol.implementation.urfu.UrFUConfiguration
import org.docx4j.wml.R.Tab

@Handler(Tab::class, UrFUConfiguration::class)
internal class TabHandler : AbstractHandler<Tab>() {
    context(VerificationContext)
    override fun handle(element: Tab) {
        render {
            append {
                create("tab") {
                    classes += "t"
                    content = "&emsp;"
                    style += {
                        width set doc.documentSettingsPart.jaxbElement.defaultTabStop.`val`.toDouble()
                        "display" set "inline-block"
                    }
                }
            }
        }
    }
}