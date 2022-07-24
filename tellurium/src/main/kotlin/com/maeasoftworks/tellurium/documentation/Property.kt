package com.maeasoftworks.tellurium.documentation

class Property(
    var name: String = "",
    var type: String = "",
    var description: String = "",
    var body: String = "",
    var enum: List<String>? = null,
    var isEnumTranslated: Boolean = false
)
