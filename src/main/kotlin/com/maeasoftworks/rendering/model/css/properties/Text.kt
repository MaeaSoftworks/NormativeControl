package com.maeasoftworks.rendering.model.css.properties

import com.maeasoftworks.rendering.utils.PIXELS_IN_POINT
import com.maeasoftworks.rendering.utils.POINTS_IN_LINES
import org.docx4j.wml.JcEnumeration

object LineHeight : Property(coefficient = POINTS_IN_LINES)

object TextIndent : Property("px", coefficient = PIXELS_IN_POINT)

object TextAlign : Property(
    converter = {
        when (it) {
            JcEnumeration.LEFT -> "left"
            JcEnumeration.RIGHT -> "right"
            JcEnumeration.CENTER -> "center"
            JcEnumeration.BOTH -> "justify"
            else -> null
        }
    }
)

object Hyphens : Property(converter = { if (it as Boolean? == true) "auto" else "none" })

object TextTransform : Property(
    converter = { value ->
        (value as Boolean?).let {
            if (it == null) null else {
                if (it) "uppercase" else null
            }
        }
    }
)

object LetterSpacing : Property("px", coefficient = PIXELS_IN_POINT)
