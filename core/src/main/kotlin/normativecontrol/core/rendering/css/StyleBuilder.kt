package normativecontrol.core.rendering.css

import normativecontrol.core.contexts.RenderingContext

object StyleBuilder {
    context(RenderingContext, Style)
    fun addRule(rule: Rule) {
        if (renderingSettings?.forceStyleInlining == true || noInline) {
            rules.add(rule)
            return
        }

        if (rule in styleCache.keys) {
            classes.add(styleCache[rule]!!)
        } else {
            val key = "s${styleCache.size}"
            globalStylesheet.styles[".$key"] = Style(noInline = true).also { it.rules.add(rule) }
            styleCache[rule] = key
            classes.add(styleCache[rule]!!)
        }
    }
}