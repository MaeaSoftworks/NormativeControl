package normativecontrol.core.rendering.css

import normativecontrol.core.contexts.RenderingContext
import normativecontrol.core.rendering.html.Constants
import org.docx4j.w14.STLigatures
import org.docx4j.wml.JcEnumeration
import org.docx4j.wml.STLineSpacingRule
import java.math.BigInteger

/**
 * List of predefined CSS properties.
 * If property is not listed here, use [String.set].
 *
 * Predefined properties often map values directly from docx4j to CSS.
 */
@Suppress("unused")
object CssProperties {
    val backgroundColor = object : Property<String>("background-color") {
        override fun converter(value: String?) = colorConverter(value)
    }

    val boxShadow = object : Property<String>("box-shadow") {}

    val boxSizing = object : Property<String>("box-sizing") {}

    val color = object : Property<String>("color") {
        override fun converter(value: String?) = colorConverter(value)
    }

    val fontFamily = object : Property<String>("font-family") {}

    val fontSize = object : DoubleProperty("font-size", "px", Constants.FONT_SCALING) {}

    val fontStyle = object : Property<Boolean?>("font-style") {
        override fun converter(value: Boolean?) = if (value == true) "italic" else null
    }

    val fontVariantCaps = object : Property<Boolean?>("font-variant-caps") {
        override fun converter(value: Boolean?) = if (value == true) "small-caps" else null
    }

    val fontVariantLigatures = object : Property<STLigatures>("font-variant-ligatures") {
        override fun converter(value: STLigatures?): String? {
            return when (value) {
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
    }

    val fontWeight = object : Property<Boolean?>("font-weight") {
        override fun converter(value: Boolean?) = if (value == true) "bold" else null
    }

    val hyphens = object : Property<Boolean?>("hyphens") {
        override fun converter(value: Boolean?) = if (value == true) "auto" else "none"
    }

    val letterSpacing = object : DoubleProperty("letter-spacing", "px", Constants.PIXELS_IN_POINT) {}

    val lineHeight = object : Property<Pair<BigInteger?, STLineSpacingRule?>>("line-height") {
        context(RenderingContext, DeclarationBlock, StyleBuilder)
        override infix fun set(value: Pair<BigInteger?, STLineSpacingRule?>?) {
            val intValue = value?.first
            val lineRule = value?.second

            when (lineRule) {
                STLineSpacingRule.AUTO -> name set (intValue?.toDouble()?.div(Constants.POINTS_IN_LINES)?.toString() ?: return)
                STLineSpacingRule.EXACT -> name set (intValue?.toDouble()?.div(Constants.POINTS_IN_LINES)?.toString() ?: return)
                STLineSpacingRule.AT_LEAST -> name set (intValue?.toString() ?: return)
                null -> return
            }
        }
    }

    val margin = object : DoubleProperty("margin", "px", 1.0) {}

    val marginBottom = object : BigIntegerProperty("margin-bottom", "px", Constants.PIXELS_IN_POINT) {}

    val marginLeft = object : BigIntegerProperty("margin-left", "px", Constants.PIXELS_IN_POINT) {}

    val marginRight = object : BigIntegerProperty("margin-right", "px", Constants.PIXELS_IN_POINT) {}

    val marginTop = object : BigIntegerProperty("margin-top", "px", Constants.PIXELS_IN_POINT) {}

    val minHeight = object : DoubleProperty("min-height", "px", Constants.PIXELS_IN_POINT) {}

    val minWidth = object : DoubleProperty("min-width", "px", Constants.PIXELS_IN_POINT) {}

    val height = object : DoubleProperty("height", "px", Constants.PIXELS_IN_POINT) {}

    val padding = object : DoubleProperty("padding") {}

    val paddingBottom = object : DoubleProperty("padding-bottom", "px", Constants.PIXELS_IN_POINT) {}

    val paddingLeft = object : DoubleProperty("padding-left", "px", Constants.PIXELS_IN_POINT) {}

    val paddingRight = object : DoubleProperty("padding-right", "px", Constants.PIXELS_IN_POINT) {}

    val paddingTop = object : DoubleProperty("padding-top", "px", Constants.PIXELS_IN_POINT) {}

    val position = object : Property<String>("position") {}

    val textAlign = object : Property<JcEnumeration>("text-align") {
        override fun converter(value: JcEnumeration?): String? {
            return when (value) {
                JcEnumeration.LEFT -> "left"
                JcEnumeration.RIGHT -> "right"
                JcEnumeration.CENTER -> "center"
                JcEnumeration.BOTH -> "justify"
                else -> null
            }
        }
    }

    val textIndent = object : BigIntegerProperty("text-indent", "px", Constants.PIXELS_IN_POINT) {}

    val textTransform = object : Property<Boolean?>("text-transform") {
        override fun converter(value: Boolean?) = if (value == true) "uppercase" else null
    }

    val width = object : DoubleProperty("width", "px", Constants.PIXELS_IN_POINT) {}

    val zIndex = object : DoubleProperty("z-index") {}
}