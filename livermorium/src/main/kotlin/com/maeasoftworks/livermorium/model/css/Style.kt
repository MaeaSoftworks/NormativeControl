package com.maeasoftworks.livermorium.model.css

import com.maeasoftworks.livermorium.model.css.properties.Property

/**
 * Representation of CSS style (contains [Rule]s and is contained in every [HTMLElement][com.maeasoftworks.livermorium.model.html.HTMLElement])
 */
class Style {
    val rules: MutableList<Rule> = mutableListOf()
    val size: Int
        get() = rules.size

    /**
     * Creates rule and adds them to list of rules
     * @param value property value
     */
    infix fun Property.set(value: Any?) {
        if (value != null && value != "null") {
            @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") //docx4j uses BigInteger from Java
            rules.add(
                Rule(
                    this.toString(),
                    this.converter?.invoke(
                        if (value is java.lang.Number && this.coefficient != null) {
                            when (this.coefficient) {
                                is Double -> value.doubleValue() / this.coefficient
                                is Int -> value.intValue() / this.coefficient
                                else -> value
                            }
                        } else {
                            value
                        }
                    ),
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
            if (r != null)
                result.append(r).append(";")
        }

        return result.toString()
    }
}