package com.maeasoftworks.livermorium.model.css.properties

import java.util.*

open class Property(
    val dimension: String? = null,
    val coefficient: Number? = null,
    val converter: ((Any?) -> String?)? = { it.toString() }
) {
    private val regex = Regex("([a-z0-9](?=[A-Z]))([A-Z])")

    override fun toString() = this::class.simpleName!!.replace(regex, "$1-$2").lowercase(Locale.getDefault())
}
