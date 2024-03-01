package ru.maeasoftworks.normativecontrol.core.css

import org.docx4j.w14.STLigatures
import org.docx4j.wml.JcEnumeration
import ru.maeasoftworks.normativecontrol.core.html.FONT_SCALING
import ru.maeasoftworks.normativecontrol.core.html.PIXELS_IN_POINT
import ru.maeasoftworks.normativecontrol.core.html.POINTS_IN_LINES

object StyleProperties {
    val boxSizing = BoxSizing
    val boxShadow = BoxShadow
    val color = Color
    val backgroundColor = BackgroundColor
    val fontFamily = FontFamily
    val fontSize = FontSize
    val fontStyle = FontStyle
    val fontWeight = FontWeight
    val fontVariantCaps = FontVariantCaps
    val fontVariantLigatures = FontVariantLigatures
    val margin = Margin
    val marginTop = MarginTop
    val marginLeft = MarginLeft
    val marginBottom = MarginBottom
    val marginRight = MarginRight
    val padding = Padding
    val paddingTop = PaddingTop
    val paddingLeft = PaddingLeft
    val paddingBottom = PaddingBottom
    val paddingRight = PaddingRight
    val position = Position
    val width = Width
    val minWidth = MinWidth
    val height = Height
    val minHeight = MinHeight
    val lineHeight = LineHeight
    val textIndent = TextIndent
    val textAlign = TextAlign
    val hyphens = Hyphens
    val textTransform = TextTransform
    val letterSpacing = LetterSpacing
    val zIndex = ZIndex
}

fun colorConverter(color: String?): String? {
    return try {
        color?.toLong(16)
        "#$color"
    } catch (e: NumberFormatException) {
        if (color != "null") color else null
    }
}

object BoxSizing : Property<String>("box-sizing")
object BoxShadow : Property<String>("box-shadow")
object Color : Property<String>("color", ::colorConverter)
object BackgroundColor : Property<String>("background-color", ::colorConverter)
object FontFamily : Property<String>("font-family")
object FontSize : DoubleProperty("font-size", "px", FONT_SCALING)
object FontStyle : Property<Boolean?>("font-style", { if (it == true) "italic" else null })
object FontWeight : Property<Boolean?>("font-weight", { if (it == true) "bold" else null })
object FontVariantCaps : Property<Boolean?>("font-variant-caps", { if (it == true) "small-caps" else null })
object Margin : DoubleProperty("margin", "px", 1.0)
object MarginTop : DoubleProperty("margin-top", "px", PIXELS_IN_POINT)
object MarginLeft : DoubleProperty("margin-left", "px", PIXELS_IN_POINT)
object MarginBottom : DoubleProperty("margin-bottom", "px", PIXELS_IN_POINT)
object MarginRight : DoubleProperty("margin-right", "px", PIXELS_IN_POINT)
object Padding : DoubleProperty("padding")
object PaddingTop : DoubleProperty("padding-top", "px", PIXELS_IN_POINT)
object PaddingLeft : DoubleProperty("padding-left", "px", PIXELS_IN_POINT)
object PaddingBottom : DoubleProperty("padding-bottom", "px", PIXELS_IN_POINT)
object PaddingRight : DoubleProperty("padding-right", "px", PIXELS_IN_POINT)
object Position : Property<String>("position")
object ZIndex : DoubleProperty("z-index")
object Width : DoubleProperty("width", "px", PIXELS_IN_POINT)
object MinWidth : DoubleProperty("min-width", "px", PIXELS_IN_POINT)
object Height : DoubleProperty("height", "px", PIXELS_IN_POINT)
object MinHeight : DoubleProperty("min-height", "px", PIXELS_IN_POINT)
object LineHeight : DoubleProperty("line-height", POINTS_IN_LINES)
object TextIndent : DoubleProperty("text-indent", "px", PIXELS_IN_POINT)
object Hyphens : Property<Boolean?>("hyphens", { if (it == true) "auto" else "none" })
object TextTransform : Property<Boolean?>("text-transform", { if (it == true) "uppercase" else null })
object LetterSpacing : DoubleProperty("letter-spacing", "px", PIXELS_IN_POINT)

object TextAlign : Property<JcEnumeration>(
    "text-align",
    {
        when (it) {
            JcEnumeration.LEFT -> "left"
            JcEnumeration.RIGHT -> "right"
            JcEnumeration.CENTER -> "center"
            JcEnumeration.BOTH -> "justify"
            else -> null
        }
    }
)

object FontVariantLigatures : Property<STLigatures>(
    "font-variant-ligatures",
    {
        when (it) {
            STLigatures.NONE -> "none"
            STLigatures.STANDARD -> "shared-ligatures"
            STLigatures.CONTEXTUAL -> "contextual"
            STLigatures.HISTORICAL -> "historical-ligatures"
            STLigatures.DISCRETIONAL -> "discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL -> "shared-ligatures contextual"
            STLigatures.STANDARD_HISTORICAL -> "shared-ligatures historical-ligatures"
            STLigatures.CONTEXTUAL_HISTORICAL -> "contextual historical-ligatures"
            STLigatures.STANDARD_DISCRETIONAL -> "shared-ligatures discretionary-ligatures"
            STLigatures.CONTEXTUAL_DISCRETIONAL -> "contextual discretionary-ligatures"
            STLigatures.HISTORICAL_DISCRETIONAL -> "historical-ligatures discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL_HISTORICAL -> "no-discretionary-ligatures"
            STLigatures.STANDARD_CONTEXTUAL_DISCRETIONAL -> "no-historical-ligatures"
            STLigatures.STANDARD_HISTORICAL_DISCRETIONAL -> "no-contextual"
            STLigatures.CONTEXTUAL_HISTORICAL_DISCRETIONAL -> "no-shared-ligatures"
            STLigatures.ALL -> null
            null -> null
        }
    }
)