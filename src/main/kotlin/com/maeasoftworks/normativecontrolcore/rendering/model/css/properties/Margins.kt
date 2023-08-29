package com.maeasoftworks.normativecontrolcore.rendering.model.css.properties

import com.maeasoftworks.normativecontrolcore.rendering.utils.PIXELS_IN_POINT

class Margin(value: Double?) : DoubleProperty("margin", value)
class MarginTop(value: Double?) : DoubleProperty("margin-top", value, PIXELS_IN_POINT, "px")
class MarginLeft(value: Double?) : DoubleProperty("margin-left", value, PIXELS_IN_POINT, "px")
class MarginBottom(value: Double?) : DoubleProperty("margin-bottom", value, PIXELS_IN_POINT, "px")
class MarginRight(value: Double?) : DoubleProperty("margin-right", value, PIXELS_IN_POINT, "px")
