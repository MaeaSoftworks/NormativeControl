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
            val r = Rule(this, value)
            rules.add(r)
            return r
        }
        return null
    }

    /**
     * Creates Rule using <code>String</code> as property name and <code>value</code> as value. Ends method chain.
     * @param value property value
     */
    infix fun String.set(value: Any) {
        if (value != "null") {
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
        }
    }

    /**
     * Converts rule value to HTML analog. Doesn't end method chain.
     * @param projector projector object which will convert
     */
    infix fun Rule<*>?.with(projector: Projector): Rule<*>? {
        if (this != null) {
            this.projector = projector
            val p = projector.project(this.rawValue)
            this.value = p
            if (p != null) {
                this.isCompleted = true
                return this
            }
            this.isCompleted = false
            return this
        }
        return null
    }

    /**
     * Converts String value to final view analog. Doesn't end method chain.
     * @param converter converter object which will convert
     */
    infix fun Rule<*>?.with(converter: Converter): Rule<*>? {
        if (this != null) {
            val c = converter.convert(this.rawValue as String?)
            this.value = c
            if (c != null) {
                this.isCompleted = true
            }
            return this
        }
        return null
    }

    infix fun <T> T.with(projector: Projector): String? {
        return projector.project(this)
    }

    infix fun <T> Rule<*>?.or(value: T): Rule<*>? {
        if (this?.value != null || this?.value != "") {
            this?.isCompleted = true
            return this
        } else {
            if (value == null) {
                this.isCompleted = false
                return this
            } else {
                this.value = this.projector?.project(value)
                this.isCompleted = true
            }
        }
        return this
    }

    infix fun Rule<*>?.or(value: String): Rule<*>? {
        if (this?.value == null || this.value == "") {
            this?.value = value
            this?.isCompleted = true
        }
        return this
    }

    operator fun plusAssign(function: Style.() -> Unit) {
        function(this)
    }

    override fun toString(): String {
        return rules.joinToString(";") { it.toString() }
    }
}