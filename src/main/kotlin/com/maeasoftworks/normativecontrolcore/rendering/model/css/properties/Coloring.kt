package com.maeasoftworks.normativecontrolcore.rendering.model.css.properties

class Color(value: String?) : Property<String>("color", value, converter = { if (it != "null") "#$it" else null })

class BackgroundColor(value: String?) : Property<String>("background-color", value, converter = { if (it != null) "#$it" else null })
