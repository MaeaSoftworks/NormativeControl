package ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties

object Color : Property<String>("color", converter = { if (it != "null") "#$it" else null })

object BackgroundColor : Property<String>("background-color", converter = { if (it != null) "#$it" else null })