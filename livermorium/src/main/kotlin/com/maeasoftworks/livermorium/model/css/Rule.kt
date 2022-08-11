package com.maeasoftworks.livermorium.model.css

data class Rule(val property: String, var value: String?, val dimension: String? = null) {
    fun serialize() = if (value != null) "$property:$value${dimension ?: ""}" else null
}