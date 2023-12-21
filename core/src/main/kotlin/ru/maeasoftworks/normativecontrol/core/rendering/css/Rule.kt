package ru.maeasoftworks.normativecontrol.core.rendering.css

data class Rule(val property: String, var value: String?, val measure: String? = null) {
    fun serialize() = if (value != null) "$property:$value${measure ?: ""}" else null
}