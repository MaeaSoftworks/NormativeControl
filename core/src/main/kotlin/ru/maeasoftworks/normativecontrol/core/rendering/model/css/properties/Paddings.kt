package ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties

import ru.maeasoftworks.normativecontrol.core.rendering.utils.PIXELS_IN_POINT

object Padding : DoubleProperty("padding")
object PaddingTop : DoubleProperty("padding-top", coefficient = PIXELS_IN_POINT, "px")
object PaddingLeft : DoubleProperty("padding-left", coefficient = PIXELS_IN_POINT, "px")
object PaddingBottom : DoubleProperty("padding-bottom", coefficient = PIXELS_IN_POINT, "px")
object PaddingRight : DoubleProperty("padding-right", coefficient = PIXELS_IN_POINT, "px")