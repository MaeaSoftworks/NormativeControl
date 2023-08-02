package com.maeasoftworks.normativecontrolcore.rendering.model.css

import com.maeasoftworks.normativecontrolcore.rendering.model.css.properties.Property

/**
 * Representation of CSS style (contains [Rule]s and is contained in every [HTMLElement][com.maeasoftworks.normativecontrolcore.rendering.model.html.HTMLElement])
 */
class Style {
    val rules: MutableList<Rule> = mutableListOf()
    val size: Int
        get() = rules.size

    /**
     * Creates rule and adds them to list of rules
     * @param value property value
     */
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

    /**
     * Creates style from rules from function
     * @param function function that creates rules
     */
    operator fun plusAssign(function: Style.() -> Unit) {
        function(this)
    }

    /**
     * Style serialization as `$rule;$rule;...`
     * @return serialized style
     */
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
