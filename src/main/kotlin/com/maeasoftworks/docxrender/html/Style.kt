package com.maeasoftworks.docxrender.html

class Style {
    private val rules: MutableList<Rule> = mutableListOf()

    infix fun String.to(value: String) {
        rules.add(Rule(this, value))
    }

    operator fun plusAssign(function: Style.() -> Unit) {
        function(this)
    }

    override fun toString(): String {
        return rules.joinToString(";") { it.toString() }
    }
}