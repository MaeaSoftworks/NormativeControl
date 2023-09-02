package core.rendering.model.css.properties

import core.rendering.utils.PIXELS_IN_POINT

class Padding(value: Double?) : DoubleProperty("padding", value)
class PaddingTop(value: Double?) : DoubleProperty("padding-top",  value, coefficient = PIXELS_IN_POINT, "px")
class PaddingLeft(value: Double?) : DoubleProperty("padding-left", value, coefficient = PIXELS_IN_POINT, "px")
class PaddingBottom(value: Double?) : DoubleProperty("padding-bottom", value, coefficient = PIXELS_IN_POINT, "px")
class PaddingRight(value: Double?) : DoubleProperty("padding-right", value, coefficient = PIXELS_IN_POINT, "px")
