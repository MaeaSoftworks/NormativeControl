package core.rendering.model.css.properties

import core.rendering.utils.PIXELS_IN_POINT
import core.rendering.utils.POINTS_IN_LINES
import org.docx4j.wml.JcEnumeration

class LineHeight(value: Double?) : DoubleProperty("line-height", value, POINTS_IN_LINES)

class TextIndent(value: Double?) : DoubleProperty("text-indent", value, PIXELS_IN_POINT, "px")

class TextAlign(value: JcEnumeration?) : Property<JcEnumeration>(
    "text-align",
    value,
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

class Hyphens(value: Boolean?) : Property<Boolean?>("hyphens", value, converter = { if (it == true) "auto" else "none" })

class TextTransform(value: Boolean?) : Property<Boolean?>("text-transform", value,
    converter = {
        if (it == null) null else {
            if (it) "uppercase" else null
        }
    }
)

class LetterSpacing(value: Double?) : DoubleProperty("letter-spacing", value, PIXELS_IN_POINT, "px")
