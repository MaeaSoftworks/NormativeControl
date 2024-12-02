package normativecontrol.core.rendering.css

import normativecontrol.core.contexts.RenderingContext

/**
 * Context object for [CssDsl].
 */
object StyleBuilder {
    /**
     * Adds rule to ruleset from context.
     */
    context(RenderingContext, DeclarationBlock)
    fun addRule(rule: Rule) {
        if (renderingSettings?.forceStyleInlining == true || noInline) {
            rules.add(rule)
            return
        }

        if (rule in styleCache.keys) {
            classes.add(styleCache[rule]!!)
        } else {
            val key = "s${styleCache.size}"
            globalStylesheet.rulesets[".$key"] = DeclarationBlock(noInline = true).also { it.rules.add(rule) }
            styleCache[rule] = key
            classes.add(styleCache[rule]!!)
        }
    }
}