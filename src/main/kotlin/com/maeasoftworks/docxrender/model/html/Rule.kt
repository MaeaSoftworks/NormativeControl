package com.maeasoftworks.docxrender.model.html

import com.maeasoftworks.docxrender.rendering.projectors.Projector

data class Rule<T>(val property: String, var rawValue: T) {
    var dimension: String? = null
    var value: String? = null
    var isCompleted = false
    var projector: Projector? = null

    override fun toString(): String {
        return if (isCompleted) "$property:${value ?: rawValue}${dimensionToString()}" else ""
    }

    private fun dimensionToString(): String {
        return dimension ?: ""
    }
}