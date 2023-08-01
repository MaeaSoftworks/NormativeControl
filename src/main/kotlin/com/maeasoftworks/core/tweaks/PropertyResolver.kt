package com.maeasoftworks.core.tweaks

import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart
import org.docx4j.wml.*

class PropertyResolver(mlPackage: WordprocessingMLPackage) {
    val styleDefinitionsPart: StyleDefinitionsPart = mlPackage.mainDocumentPart.styleDefinitionsPart
    private val resolver = PropertyResolver(mlPackage)
    val dPPr: PPr = resolver.documentDefaultPPr
    val dRPr: RPr = resolver.documentDefaultRPr

    /**
     * Fixed & optimized analog for [PropertyResolver.getEffectivePPr].
     * Returns last not null property value of [path] in priority:
     *
     * 1. Document default paragraph properties.
     * 2. Default paragraph style.
     * 3. `<style name>` PPr.
     * 4. Paragraph PPr.
     *
     * @param path property in lambda expression
     * @return property value
     */
    inline fun <T> getActualProperty(p: P, path: PPr.() -> T?): T? {
        return p.pPr?.path()
            ?: styleDefinitionsPart.getStyleById(p.pPr?.pStyle?.`val`)?.pPr?.path()
            ?: dPPr.path()
            ?: styleDefinitionsPart.defaultParagraphStyle.pPr.path()
    }

    // TODO: add support for composite objects
    inline fun <T> getActualProperty(r: R, path: RPr.() -> T?): T? {
        val p = if (r.parent is P) r.parent as P else null
        val pStyle = styleDefinitionsPart.getStyleById(p?.pPr?.pStyle?.`val`)
        val rStyle = styleDefinitionsPart.getStyleById(r.rPr?.rStyle?.`val`)

        return r.rPr?.path()
            ?: rStyle?.rPr?.path()
            ?: pStyle?.rPr?.path()
            ?: styleDefinitionsPart.getStyleById("${p?.pPr?.pStyle?.`val`}Char")?.rPr?.path()
            ?: styleDefinitionsPart.getStyleById(p?.pPr?.pStyle?.`val`)?.rPr?.path()
            ?: getFirstValueInBasedStyles(pStyle, path)
            ?: styleDefinitionsPart.defaultCharacterStyle.rPr?.path()
            ?: styleDefinitionsPart.defaultParagraphStyle.rPr?.path()
            ?: dRPr.path()
    }

    inline fun <T> getFirstValueInBasedStyles(rPrStyle: Style?, path: RPr.() -> T?): T? {
        var current: Style? = rPrStyle
        while (current != null) {
            current = styleDefinitionsPart.getStyleById(current.basedOn?.`val`)
            val value = current?.rPr?.path()
            if (value != null) {
                return value
            }
        }
        return null
    }
}