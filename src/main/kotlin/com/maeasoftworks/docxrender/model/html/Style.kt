package com.maeasoftworks.docxrender.model.html

import com.maeasoftworks.docxrender.rendering.converters.Converter
import com.maeasoftworks.docxrender.rendering.projectors.Projector

class Style {
    private val rules: MutableList<Rule<*>> = mutableListOf()
    val size: Int
        get() = rules.size

    /**
     * Creates Rule using <code>String</code> as property name and <code>value</code> as value. Doesn't seal rule, can start method chain.
     * @param value property value
     * @return created rule
     */
    infix fun String.to(value: Any?): Rule<*>? {
        if (value != null) {
            return Rule(this, value)
        }
        return null
    }

    /**
     * Creates Rule using <code>String</code> as property name and <code>value</code> as value. Ends method chain.
     * @param value property value
     */
    infix fun String.set(value: Any?) {
        if (value != null && value != "null") {
            val r = Rule(this, value)
            rules.add(r)
            r.isCompleted = true
        }
    }

    /**
     * Adds dimension to rule. Ends method chain.
     * @param dimension dimension that will be added to rule
     */
    infix fun Rule<*>?.with(dimension: String) {
        if (this != null) {
            this.dimension = dimension
            this.isCompleted = true
            rules.add(this)
        }
    }

    /**
     * Converts rule value to HTML analog. Ends method chain.
     * @param projector projector object which will convert
     */
    infix fun Rule<*>?.with(projector: Projector) {
        if (this != null) {
            val p = projector.project(this.rawValue)
            this.value = p
            this.isCompleted = true
            rules.add(this)
        }
    }

    /**
     * Converts String value to final view analog. Ends method chain.
     * @param converter converter object which will convert
     */
    infix fun Rule<*>?.with(converter: Converter) {
        if (this != null) {
            val c = converter.convert(this.rawValue as String?)
            this.value = c
            this.isCompleted = true
            if (c != null) {
                rules.add(this)
            }
        }
    }

    operator fun plusAssign(function: Style.() -> Unit) {
        function(this)
    }

    override fun toString(): String {
        return rules.joinToString(";") { it.toString() }
    }
}