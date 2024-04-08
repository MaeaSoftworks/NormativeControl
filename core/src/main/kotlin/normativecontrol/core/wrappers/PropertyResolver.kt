package normativecontrol.core.wrappers

import normativecontrol.core.contexts.VerificationContext
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart
import org.docx4j.wml.*

class PropertyResolver(mlPackage: WordprocessingMLPackage) {
    val styleDefinitionsPart: StyleDefinitionsPart = mlPackage.mainDocumentPart.styleDefinitionsPart
    val numbering: Numbering? = mlPackage.mainDocumentPart.numberingDefinitionsPart?.jaxbElement
    val dPPr: PPr
    val dRPr: RPr

    init {
        org.docx4j.model.PropertyResolver(mlPackage).apply {
            dPPr = this.documentDefaultPPr
            dRPr = this.documentDefaultRPr
        }
    }

    fun resolveNumberingStyle(p: P): Lvl? {
        if (p.pPr.numPr == null) return null
        val abstractNumId = numbering?.num?.firstOrNull { it.numId == p.pPr.numPr.numId?.`val` }?.abstractNumId?.`val`
        val abstract = numbering?.abstractNum?.firstOrNull { it.abstractNumId == abstractNumId }
        return p.pPr.numPr.ilvl?.`val`?.toInt()?.let { abstract?.lvl?.get(it) }
    }

    inline fun <T> getActualProperty(p: P, path: PPr.() -> T?): T? {
        val pStyle = styleDefinitionsPart.getStyleById(p.pPr?.pStyle?.`val`)
        return p.pPr?.path()
            ?: resolveNumberingStyle(p)?.pPr?.path()
            ?: styleDefinitionsPart.getStyleById(p.pPr?.pStyle?.`val`)?.pPr?.path()
            ?: getFirstValueInBasedStylesP(pStyle, path)
            ?: dPPr.path()
            ?: styleDefinitionsPart.defaultParagraphStyle?.pPr?.path()
    }

    inline fun <T> getActualProperty(r: R, path: RPr.() -> T?): T? {
        val p = r.parent as? P
        val pStyle = styleDefinitionsPart.getStyleById(p?.pPr?.pStyle?.`val`)
        val rStyle = styleDefinitionsPart.getStyleById(r.rPr?.rStyle?.`val`)

        return r.rPr?.path()
            ?: rStyle?.rPr?.path()
            ?: pStyle?.rPr?.path()
            ?: p?.let { resolveNumberingStyle(it)?.rPr?.path() }
            ?: styleDefinitionsPart.getStyleById("${p?.pPr?.pStyle?.`val`}Char")?.rPr?.path()
            ?: styleDefinitionsPart.getStyleById(p?.pPr?.pStyle?.`val`)?.rPr?.path()
            ?: getFirstValueInBasedStylesR(pStyle, path)
            ?: styleDefinitionsPart.defaultCharacterStyle.rPr?.path()
            ?: styleDefinitionsPart.defaultParagraphStyle.rPr?.path()
            ?: dRPr.path()
    }

    inline fun <T> getFirstValueInBasedStylesR(rPrStyle: Style?, path: RPr.() -> T?): T? {
        var current: Style? = rPrStyle
        while (current != null) {
            current = styleDefinitionsPart.getStyleById(current.basedOn?.`val`)
            return current?.rPr?.path() ?: continue
        }
        return null
    }

    inline fun <T> getFirstValueInBasedStylesP(pPrStyle: Style?, path: PPr.() -> T?): T? {
        var current: Style? = pPrStyle
        while (current != null) {
            current = styleDefinitionsPart.getStyleById(current.basedOn?.`val`)
            return current?.pPr?.path() ?: continue
        }
        return null
    }
}