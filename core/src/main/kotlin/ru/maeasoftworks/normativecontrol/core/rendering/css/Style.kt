package ru.maeasoftworks.normativecontrol.core.rendering.css

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

    inline operator fun plusAssign(fn: Block.() -> Unit) {
        rules = Block().also(fn).rules
    }

    @Suppress("UNUSED")
    class Block {
        val rules: MutableList<Rule> = mutableListOf()
        var boxSizing = BoxSizing
        var boxShadow = BoxShadow
        var color = Color
        var backgroundColor = BackgroundColor
        var fontFamily = FontFamily
        var fontSize = FontSize
        var fontStyle = FontStyle
        var fontWeight = FontWeight
        var fontVariantCaps = FontVariantCaps
        var fontVariantLigatures = FontVariantLigatures
        var margin = Margin
        var marginTop = MarginTop
        var marginLeft = MarginLeft
        var marginBottom = MarginBottom
        var marginRight = MarginRight
        var padding = Padding
        var paddingTop = PaddingTop
        var paddingLeft = PaddingLeft
        var paddingBottom = PaddingBottom
        var paddingRight = PaddingRight
        var position = Position
        var width = Width
        var minWidth = MinWidth
        var height = Height
        var minHeight = MinHeight
        var lineHeight = LineHeight
        var textIndent = TextIndent
        var textAlign = TextAlign
        var hyphens = Hyphens
        var textTransform = TextTransform
        var letterSpacing = LetterSpacing
        var zIndex = ZIndex

        infix fun <T> Property<T>.set(value: T?) {
            if (value != null) {
                val v = this.converter(value)
                if (v != null) {
                    rules.add(Rule(name, v, measure))
                }
            }
        }
    }
}