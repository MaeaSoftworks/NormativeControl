package com.maeasoftworks.rendering.model.css.properties

import com.maeasoftworks.rendering.utils.PIXELS_IN_POINT

object Padding : Property()
object PaddingTop : Property("px", coefficient = PIXELS_IN_POINT)
object PaddingLeft : Property("px", coefficient = PIXELS_IN_POINT)
object PaddingBottom : Property("px", coefficient = PIXELS_IN_POINT)
object PaddingRight : Property("px", coefficient = PIXELS_IN_POINT)