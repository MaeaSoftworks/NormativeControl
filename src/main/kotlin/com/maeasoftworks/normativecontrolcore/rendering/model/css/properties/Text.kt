package com.maeasoftworks.normativecontrolcore.rendering.model.css.properties

import com.maeasoftworks.normativecontrolcore.rendering.utils.PIXELS_IN_POINT
import com.maeasoftworks.normativecontrolcore.rendering.utils.POINTS_IN_LINES
import org.docx4j.wml.JcEnumeration

object LineHeight : DoubleProperty(coefficient = POINTS_IN_LINES)

object TextIndent : DoubleProperty("px", PIXELS_IN_POINT)

object TextAlign : Property<JcEnumeration>(
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

object Hyphens : Property<Boolean?>(converter = { if (it == true) "auto" else "none" })

object TextTransform : Property<Boolean?>(
    converter = {
        if (it == null) null else {
            if (it) "uppercase" else null
        }
    }
)

object LetterSpacing : DoubleProperty("px", coefficient = PIXELS_IN_POINT)
