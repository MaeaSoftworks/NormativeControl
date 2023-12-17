package ru.maeasoftworks.normativecontrol.core.rendering.model.css

import ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties.*

class Style {
    var rules: MutableList<Rule> = mutableListOf()
    val size: Int
        get() = rules.size

    override fun toString(): String {
        val result = StringBuilder()
        for (rule in rules) {
            val r = rule.serialize()
            if (r != null) {
                result.append(r).append(";")
            }
        }

        return result.toString()
    }

    inline operator fun invoke(fn: Builder.() -> Unit) {
        rules = Builder().also(fn).rules
    }

    class Builder {
        val rules: MutableList<Rule> = mutableListOf()
        var boxSizing = StyleProperty(BoxSizing)
        var boxShadow = StyleProperty(BoxShadow)
        var color = StyleProperty(Color)
        var backgroundColor = StyleProperty(BackgroundColor)
        var fontFamily = StyleProperty(FontFamily)
        var fontSize = StyleProperty(FontSize)
        var fontStyle = StyleProperty(FontStyle)
        var fontWeight = StyleProperty(FontWeight)
        var fontVariantCaps = StyleProperty(FontVariantCaps)
        var fontVariantLigatures = StyleProperty(FontVariantLigatures)
        var margin = StyleProperty(Margin)
        var marginTop = StyleProperty(MarginTop)
        var marginLeft = StyleProperty(MarginLeft)
        var marginBottom = StyleProperty(MarginBottom)
        var marginRight = StyleProperty(MarginRight)
        var padding = StyleProperty(Padding)
        var paddingTop = StyleProperty(PaddingTop)
        var paddingLeft = StyleProperty(PaddingLeft)
        var paddingBottom = StyleProperty(PaddingBottom)
        var paddingRight = StyleProperty(PaddingRight)
        var position = StyleProperty(Position)
        var width = StyleProperty(Width)
        var minWidth = StyleProperty(MinWidth)
        var height = StyleProperty(Height)
        var minHeight = StyleProperty(MinHeight)
        var lineHeight = StyleProperty(LineHeight)
        var textIndent = StyleProperty(TextIndent)
        var textAlign = StyleProperty(TextAlign)
        var hyphens = StyleProperty(Hyphens)
        var textTransform = StyleProperty(TextTransform)
        var letterSpacing = StyleProperty(LetterSpacing)
        var zIndex = StyleProperty(ZIndex)

        infix fun <T> StyleProperty<T>.`=`(value: T?) {
            if (value != null) {
                val v = this.property.converter(value)
                if (v != null) {
                    rules.add(Rule(property.name, v, property.measure))
                }
            }
        }

        data class StyleProperty<T>(val property: Property<T>)
    }
}