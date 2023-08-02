package com.maeasoftworks.normativecontrolcore.rendering.model.css.properties

import com.maeasoftworks.normativecontrolcore.rendering.utils.PIXELS_IN_POINT

object Padding : DoubleProperty()
object PaddingTop : DoubleProperty("px", coefficient = PIXELS_IN_POINT)
object PaddingLeft : DoubleProperty("px", coefficient = PIXELS_IN_POINT)
object PaddingBottom : DoubleProperty("px", coefficient = PIXELS_IN_POINT)
object PaddingRight : DoubleProperty("px", coefficient = PIXELS_IN_POINT)
