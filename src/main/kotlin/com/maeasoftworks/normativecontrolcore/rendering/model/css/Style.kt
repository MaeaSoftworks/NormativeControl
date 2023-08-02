package com.maeasoftworks.normativecontrolcore.rendering.model.css

import com.maeasoftworks.normativecontrolcore.rendering.model.css.properties.Property

class Style {
    val rules: MutableList<Rule> = mutableListOf()
    val size: Int
        get() = rules.size

    infix fun <T> Property<T>.set(value: T?) {
        if (value != null && value != "null") {
            rules.add(
                Rule(
                    this.toString(),
                    this.converter(value),
                    this.measure
                )
            )
        }
    }

    operator fun plusAssign(function: Style.() -> Unit) {
        function(this)
    }

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
}
