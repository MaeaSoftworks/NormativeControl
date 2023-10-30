package ru.maeasoftworks.normativecontrol.core.rendering.model.css

import ru.maeasoftworks.normativecontrol.core.rendering.model.css.properties.*
import kotlin.reflect.KProperty

class Style {
    private val rules: MutableList<Rule> = mutableListOf()
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

    class Builder {
        private val style = Style()
        var boxSizing: BoxSizing? by StyleProperty
        var boxShadow: BoxShadow? by StyleProperty
        var color: Color? by StyleProperty
        var backgroundColor: BackgroundColor? by StyleProperty
        var fontFamily: FontFamily? by StyleProperty
        var fontSize: FontSize? by StyleProperty
        var fontStyle: FontStyle? by StyleProperty
        var fontWeight: FontWeight? by StyleProperty
        var fontVariantCaps: FontVariantCaps? by StyleProperty
        var fontVariantLigatures: FontVariantLigatures? by StyleProperty
        var margin: Margin? by StyleProperty
        var marginTop: MarginTop? by StyleProperty
        var marginLeft: MarginLeft? by StyleProperty
        var marginBottom: MarginBottom? by StyleProperty
        var marginRight: MarginRight? by StyleProperty
        var padding: Padding? by StyleProperty
        var paddingTop: PaddingTop? by StyleProperty
        var paddingLeft: PaddingLeft? by StyleProperty
        var paddingBottom: PaddingBottom? by StyleProperty
        var paddingRight: PaddingRight? by StyleProperty
        var position: Position? by StyleProperty
        var width: Width? by StyleProperty
        var minWidth: MinWidth? by StyleProperty
        var height: Height? by StyleProperty
        var minHeight: MinHeight? by StyleProperty
        var lineHeight: LineHeight? by StyleProperty
        var textIndent: TextIndent? by StyleProperty
        var textAlign: TextAlign? by StyleProperty
        var hyphens: Hyphens? by StyleProperty
        var textTransform: TextTransform? by StyleProperty
        var letterSpacing: LetterSpacing? by StyleProperty
        var zIndex: ZIndex? by StyleProperty

        fun build() = style

        object StyleProperty {
            operator fun <T> getValue(receiver: Builder, property: KProperty<*>): T {
                throw NotImplementedError()
            }

            operator fun <O, T : Property<O>> setValue(receiver: Builder, kProperty: KProperty<*>, value: T?) {
                if (value != null) {
                    val v = value.converter(value.value)
                    if (v != null) {
                        receiver.style.rules.add(Rule(value.name, v, value.measure))
                    }
                }
            }
        }
    }
}
