package ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties

import org.docx4j.wml.JcEnumeration
import ru.maeasoftworks.normativecontrol.core.rendering.utils.PIXELS_IN_POINT
import ru.maeasoftworks.normativecontrol.core.rendering.utils.POINTS_IN_LINES

object LineHeight : DoubleProperty("line-height", POINTS_IN_LINES)

object TextIndent : DoubleProperty("text-indent", PIXELS_IN_POINT, "px")

object TextAlign : Property<JcEnumeration>(
    "text-align",
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

object Hyphens : Property<Boolean?>("hyphens", converter = { if (it == true) "auto" else "none" })

object TextTransform : Property<Boolean?>(
    "text-transform",
    converter = {
        if (it == null) {
            null
        } else {
            if (it) "uppercase" else null
        }
    }
)

object LetterSpacing : DoubleProperty("letter-spacing", PIXELS_IN_POINT, "px")