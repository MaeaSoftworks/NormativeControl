package com.maeasoftworks.rendering.model.css.properties

import com.maeasoftworks.rendering.utils.PIXELS_IN_POINT

object Margin : Property()
object MarginTop : Property("px", coefficient = PIXELS_IN_POINT)
object MarginLeft : Property("px", coefficient = PIXELS_IN_POINT)
object MarginBottom : Property("px", coefficient = PIXELS_IN_POINT)
object MarginRight : Property("px", coefficient = PIXELS_IN_POINT)
