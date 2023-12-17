package ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties

import ru.maeasoftworks.normativecontrol.core.rendering.utils.PIXELS_IN_POINT

object Margin : DoubleProperty("margin")
object MarginTop : DoubleProperty("margin-top", PIXELS_IN_POINT, "px")
object MarginLeft : DoubleProperty("margin-left", PIXELS_IN_POINT, "px")
object MarginBottom : DoubleProperty("margin-bottom", PIXELS_IN_POINT, "px")
object MarginRight : DoubleProperty("margin-right", PIXELS_IN_POINT, "px")