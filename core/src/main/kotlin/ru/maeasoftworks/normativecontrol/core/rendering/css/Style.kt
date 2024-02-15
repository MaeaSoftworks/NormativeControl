package ru.maeasoftworks.normativecontrol.core.rendering.css

import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext

class Style(
    val classes: MutableList<String> = mutableListOf(),
    val noInline: Boolean = false
) {
    val rules: MutableList<Rule> by lazy { mutableListOf() }
    val size: Int
        get() = rules.size

    override fun toString(): String {
        val result = StringBuilder()
        for (rule in rules) {
            val r = rule.serialize()
            if (r != null) {
                result.append(r).append(";")
            }
        }

        return result.toString()
    }

    context(VerificationContext)
    operator fun plusAssign(fn: context(Style, StyleProperties, StyleBuilder) () -> Unit) {
        fn(this, StyleProperties, StyleBuilder)
    }
}