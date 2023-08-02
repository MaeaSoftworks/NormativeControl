package com.maeasoftworks.normativecontrolcore.rendering.model.css.properties

object Color : Property<String>(converter = { if (it != "null") "#$it" else null })

object BackgroundColor : Property<String>(converter = { if (it != null) "#$it" else null })
