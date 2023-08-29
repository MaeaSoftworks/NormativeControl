package com.maeasoftworks.normativecontrolcore.rendering.model.css.properties

import com.maeasoftworks.normativecontrolcore.rendering.utils.PIXELS_IN_POINT

class Width(value: Double?) : DoubleProperty("width", value, PIXELS_IN_POINT, "px")
class MinWidth(value: Double?) : DoubleProperty("min-width", value, PIXELS_IN_POINT, "px")

class Height(value: Double?) : DoubleProperty("height", value, PIXELS_IN_POINT, "px")
class MinHeight(value: Double?) : DoubleProperty("min-height", value, PIXELS_IN_POINT, "px")
