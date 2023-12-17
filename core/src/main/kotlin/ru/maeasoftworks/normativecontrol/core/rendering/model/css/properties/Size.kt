package ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties

import ru.maeasoftworks.normativecontrol.core.rendering.utils.PIXELS_IN_POINT

object Width : DoubleProperty("width", PIXELS_IN_POINT, "px")
object MinWidth : DoubleProperty("min-width", PIXELS_IN_POINT, "px")

object Height : DoubleProperty("height", PIXELS_IN_POINT, "px")
object MinHeight : DoubleProperty("min-height", PIXELS_IN_POINT, "px")