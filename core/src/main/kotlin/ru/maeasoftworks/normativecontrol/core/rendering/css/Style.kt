package ru.maeasoftworks.normativecontrol.core.rendering.css

import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

class Style(
    val classes: MutableList<String>? = null,
    val disableCaching: Boolean = false
) {
    val rules: MutableList<Rule> by lazy { mutableListOf() }
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
        val block = Block(disableCaching).also(fn)
        rules.addAll(block.rules)
        classes?.addAll(block.classes)
    }

    @Suppress("UNUSED")
    class Block(private val disableCaching: Boolean) {
        val rules: MutableList<Rule> = mutableListOf()
        val classes: MutableList<String> = mutableListOf()
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

        context(VerificationContext)
        infix fun <T> Property<T>.set(value: T?) {
            if (value != null) {
                val v = this.converter(value)
                if (v != null) {
                    addRule(Rule(name, v, measure))
                }
            }
        }

        context(VerificationContext)
        infix fun String.set(value: String) {
            addRule(Rule(this, value))
        }

        context(VerificationContext)
        private fun addRule(rule: Rule) {
            if (disableCaching) {
                rules.add(rule)
                return
            }

            if (rule in render.styleCache.keys) {
                classes.add(render.styleCache[rule]!!)
            } else {
                val key = "s${render.styleCache.size}"
                render.globalStyle.styles[".$key"] = Style(disableCaching = true).also { it.rules.add(rule) }
                render.styleCache[rule] = key
                classes.add(render.styleCache[rule]!!)
            }
        }
    }
}