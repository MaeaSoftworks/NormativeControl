package com.maeasoftworks.livermorium.model.html

data class Rule<T>(val property: String, var rawValue: T) {
    var dimension: String? = null
    var value: String? = null
    var isCompleted = false

    override fun toString(): String {
        return if (isCompleted) "$property:${value ?: rawValue}${dimensionToString()}" else ""
    }

    private fun dimensionToString(): String {
        return dimension ?: ""
    }
}