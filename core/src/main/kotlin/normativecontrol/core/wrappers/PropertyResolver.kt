package normativecontrol.core.wrappers

import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart
import org.docx4j.wml.*
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

class PropertyResolver(mlPackage: WordprocessingMLPackage) {
    val styleDefinitionsPart: StyleDefinitionsPart = mlPackage.mainDocumentPart.styleDefinitionsPart
    private val stylesMap = styleDefinitionsPart.jaxbElement.style.associateBy { it.styleId }
    private val numbering: Numbering? = mlPackage.mainDocumentPart.numberingDefinitionsPart?.jaxbElement
    val dPPr: PPr
    val dRPr: RPr

    @OptIn(InternalConstructor::class)
    val numberingResolver = NumberingResolver()

    init {
        org.docx4j.model.PropertyResolver(mlPackage).apply {
            dPPr = this.documentDefaultPPr
            dRPr = this.documentDefaultRPr
        }
    }

    fun resolveNumberingStyle(pPr: PPr?): Lvl? {
        if (pPr?.numPr == null) return null
        val abstractNumId = numberingResolver.numIdMap?.get(pPr.numPr?.numId?.`val`)?.abstractNumId?.`val`
        val abstract = numberingResolver.abstractIdMap?.get(abstractNumId)
        return pPr.numPr.ilvl?.`val`?.toInt()?.let { abstract?.lvl?.get(it) }
    }

    inline fun <T> getActualProperty(pPr: PPr?, path: PPr.() -> T?): T? {
        val pStyle = getStyleByIdOptimized(pPr?.pStyle?.`val`)
        return pPr?.path()
            ?: resolveNumberingStyle(pPr)?.pPr?.path()
            ?: getStyleByIdOptimized(pPr?.pStyle?.`val`)?.pPr?.path()
            ?: getFirstValueInBasedStylesP(pStyle, path)
            ?: dPPr.path()
            ?: styleDefinitionsPart.defaultParagraphStyle?.pPr?.path()
    }

    inline fun <T> getActualProperty(r: R?, path: RPr.() -> T?): T? {
        val rPr = r?.rPr
        val p = r?.parent as? P
        val pStyle = getStyleByIdOptimized(p?.pPr?.pStyle?.`val`)
        val rStyle = getStyleByIdOptimized(rPr?.rStyle?.`val`)

        return rPr?.path()
            ?: rStyle?.rPr?.path()
            ?: pStyle?.rPr?.path()
            ?: getStyleByIdOptimized("${p?.pPr?.pStyle?.`val`}Char")?.rPr?.path()
            ?: getStyleByIdOptimized(p?.pPr?.pStyle?.`val`)?.rPr?.path()
            ?: getFirstValueInBasedStylesR(pStyle, path)
            ?: styleDefinitionsPart.defaultCharacterStyle.rPr?.path()
            ?: styleDefinitionsPart.defaultParagraphStyle.rPr?.path()
            ?: dRPr.path()
            ?: resolveNumberingStyle(p?.pPr)?.rPr?.path()
    }

    inline fun <T> getFirstValueInBasedStylesR(rPrStyle: Style?, path: RPr.() -> T?): T? {
        var current: Style? = rPrStyle
        while (current != null) {
            current = getStyleByIdOptimized(current.basedOn?.`val`)
            return current?.rPr?.path() ?: continue
        }
        return null
    }

    inline fun <T> getFirstValueInBasedStylesP(pPrStyle: Style?, path: PPr.() -> T?): T? {
        var current: Style? = pPrStyle
        while (current != null) {
            current = getStyleByIdOptimized(current.basedOn?.`val`)
            return current?.pPr?.path() ?: continue
        }
        return null
    }

    fun getStyleByIdOptimized(id: String?): Style? {
        if (id == null) return null
        return this.stylesMap[id]
    }

    inner class NumberingResolver @InternalConstructor constructor() {
        val numIdMap = numbering?.num?.associateBy { it.numId }
        val abstractIdMap = numbering?.abstractNum?.associateBy { it.abstractNumId }
    }

    @RequiresOptIn(level = RequiresOptIn.Level.ERROR)
    private annotation class InternalConstructor
}