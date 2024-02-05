package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart
import org.docx4j.wml.*

class PropertyResolver(mlPackage: WordprocessingMLPackage) {
    val styleDefinitionsPart: StyleDefinitionsPart = mlPackage.mainDocumentPart.styleDefinitionsPart
    val dPPr: PPr
    val dRPr: RPr

    init {
        org.docx4j.model.PropertyResolver(mlPackage).apply {
            dPPr = this.documentDefaultPPr
            dRPr = this.documentDefaultRPr
        }
    }

    inline fun <T> getActualProperty(pPr: PPr?, path: PPr.() -> T?): T? {
        return pPr?.path()
            ?: styleDefinitionsPart.getStyleById(pPr?.pStyle?.`val`)?.pPr?.path()
            ?: dPPr.path()
            ?: styleDefinitionsPart.defaultParagraphStyle?.pPr?.path()
    }

    inline fun <T> getActualProperty(rPr: RPr?, path: RPr.() -> T?): T? {
        val p = (rPr?.parent as? R)?.parent as? P
        val pStyle = styleDefinitionsPart.getStyleById(p?.pPr?.pStyle?.`val`)
        val rStyle = styleDefinitionsPart.getStyleById(rPr?.rStyle?.`val`)

        return rPr?.path()
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