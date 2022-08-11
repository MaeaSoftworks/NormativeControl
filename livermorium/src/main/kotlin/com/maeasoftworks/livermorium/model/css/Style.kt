package com.maeasoftworks.livermorium.model.css

import com.maeasoftworks.livermorium.model.css.properties.Property


class Style {
    val rules: MutableList<Rule> = mutableListOf()
    val size: Int
        get() = rules.size

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
                    this.dimension
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
            if (r != null)
                result.append(r).append(";")
        }

        return result.toString()
    }
}