package com.maeasoftworks.normativecontrol.dto.documentation.objects

class PropertyInfo(
    var name: String = "",
    var type: String = "",
    var description: String = "",
    var body: String = "",
    var enum: List<String>? = null,
    var isEnumTranslated: Boolean = false
)
