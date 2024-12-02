package normativecontrol.core.handlers

import normativecontrol.core.contexts.VerificationContext

internal class ExtendedHandler<T : Any>(
    private val extender: AbstractHandler<T>,
    private val extended: AbstractHandler<T>
) : AbstractHandler<T>() {
    context(VerificationContext)
    override fun handle(element: T) {
        extended.handleElement(element)
        extender.handleElement(element)
    }
}