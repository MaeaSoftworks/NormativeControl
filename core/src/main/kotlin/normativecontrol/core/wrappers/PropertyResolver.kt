package normativecontrol.core.wrappers

import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.exceptions.CoreException
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart
import org.docx4j.wml.*
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

/**
 * Big magic box of this whole project.
 * Resolves all properties of document elements lazily, without reflections and deep copying.
 * @param mlPackage current document
 */
context(VerificationContext)
class PropertyResolver(mlPackage: WordprocessingMLPackage) {
    @PublishedApi
    internal val styleDefinitionsPart: StyleDefinitionsPart = mlPackage.mainDocumentPart.styleDefinitionsPart

    @PublishedApi
    internal val dPPr: PPr

    @PublishedApi
    internal val dRPr: RPr

    private val resolver: org.docx4j.model.PropertyResolver
    private val stylesMap: Map<String, Style>?
    private val numbering: Numbering? = mlPackage.mainDocumentPart.numberingDefinitionsPart?.jaxbElement
    private val numIdMap = numbering?.num?.associateBy { it.numId }
    private val abstractIdMap = numbering?.abstractNum?.associateBy { it.abstractNumId }

    init {
        if (styleDefinitionsPart.jaxbElement?.style == null) {
            throw CoreException.IncorrectStylesPart(locale)
        }
        stylesMap = styleDefinitionsPart.jaxbElement?.style?.associateBy { it.styleId }
        resolver = org.docx4j.model.PropertyResolver(mlPackage)
        dPPr = resolver.documentDefaultPPr
        dRPr = resolver.documentDefaultRPr
    }

    /**
     * Resolves actual numbering style of PPr.
     * @param pPr PPr of paragraph with numbering
     * @return [Lvl] object if paragraph has numbering, otherwise null
     */
    fun resolveNumberingStyle(pPr: PPr?): Lvl? {
        if (pPr?.numPr == null) return null
        val abstractNumId = numIdMap?.get(pPr.numPr?.numId?.`val`)?.abstractNumId?.`val`
        val abstract = abstractIdMap?.get(abstractNumId)
        return pPr.numPr.ilvl?.`val`?.toInt()?.let { abstract?.lvl?.getOrNull(it) }
    }

    /**
     * Resolves actual value of property passed in [path] of paragraph's PPr.
     * @param pPr paragraph properties
     * @param path member of PPr whose value needs to be found
     * @return actual value of member
     */
    inline fun <T> getActualProperty(pPr: PPr?, path: PPr.() -> T?): T? {
        val pStyle = getStyleByIdOptimized(pPr?.pStyle?.`val`)
        return pPr?.path()
            ?: resolveNumberingStyle(pPr)?.pPr?.path()
            ?: getStyleByIdOptimized(pPr?.pStyle?.`val`)?.pPr?.path()
            ?: getFirstValueInBasedStylesP(pStyle, path)
            ?: dPPr.path()
            ?: styleDefinitionsPart.defaultParagraphStyle?.pPr?.path()
    }

    /**
     * Resolves actual value of property passed in [path] of run's RPr.
     * @param r run
     * @param path member of RPr whose value needs to be found
     * @return actual value of member
     */
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

    @PublishedApi
    internal inline fun <T> getFirstValueInBasedStylesR(rPrStyle: Style?, path: RPr.() -> T?): T? {
        var current: Style? = rPrStyle
        while (current != null) {
            current = getStyleByIdOptimized(current.basedOn?.`val`)
            return current?.rPr?.path() ?: continue
        }
        return null
    }

    @PublishedApi
    internal inline fun <T> getFirstValueInBasedStylesP(pPrStyle: Style?, path: PPr.() -> T?): T? {
        var current: Style? = pPrStyle
        while (current != null) {
            current = getStyleByIdOptimized(current.basedOn?.`val`)
            return current?.pPr?.path() ?: continue
        }
        return null
    }

    @PublishedApi
    internal fun getStyleByIdOptimized(id: String?): Style? {
        if (id == null) return null
        return stylesMap?.get(id)
    }
}