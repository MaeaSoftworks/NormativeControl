package normativecontrol.core.predefined

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import org.docx4j.wml.ContentAccessor

abstract class ContentAccessorHandler<A: ContentAccessor>: AbstractHandler<A>() {
    context(VerificationContext)
    override fun handle(element: A) {
        render.inLastElementScope {
            element.iterate { child, _ ->
                runtime.handlers[child]?.handleElement(child)
            }
        }
    }
}