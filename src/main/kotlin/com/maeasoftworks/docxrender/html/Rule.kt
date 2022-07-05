package com.maeasoftworks.docxrender.html

data class Rule(val property: String, val value: String) {
    override fun toString(): String {
        return "$property:$value"
    }
}