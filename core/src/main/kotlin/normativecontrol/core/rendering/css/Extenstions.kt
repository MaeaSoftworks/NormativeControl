package normativecontrol.core.rendering.css

import normativecontrol.core.contexts.RenderingContext

context(RenderingContext, DeclarationBlock, StyleBuilder)
@CssDsl
infix fun String.set(value: String) {
    addRule(Rule(this, value))
}