package ru.maeasoftworks.normativecontrol.core.rendering.css

import ru.maeasoftworks.normativecontrol.core.configurations.VerificationConfiguration
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext

object StyleBuilder {
    private val forceStyleInlining by lazy { VerificationConfiguration.forceStyleInlining }

    context(VerificationContext, Style)
    infix fun <T> Property<T>.set(value: T?) {
        if (value != null) {
            val v = this.converter(value)
            if (v != null) {
                addRule(Rule(name, v, measure))
            }
        }
    }

    context(VerificationContext, Style)
    infix fun String.set(value: String) {
        addRule(Rule(this, value))
    }

    context(VerificationContext, Style)
    private fun addRule(rule: Rule) {
        if (forceStyleInlining || noInline) {
            rules.add(rule)
            return
        }

        if (rule in render.styleCache.keys) {
            classes.add(render.styleCache[rule]!!)
        } else {
            val key = "s${render.styleCache.size}"
            render.globalStyle.styles[".$key"] = Style(noInline = true).also { it.rules.add(rule) }
            render.styleCache[rule] = key
            classes.add(render.styleCache[rule]!!)
        }
    }
}