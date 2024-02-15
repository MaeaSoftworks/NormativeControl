package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.wml.*
import org.docx4j.wml.PPrBase.*
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext

@JvmInline
@Suppress("unused")
value class ResolvedPPr(private val p: P) {
    context(VerificationContext)
    val pStyle: PStyle?
        get() = resolver.getActualProperty(p) { pStyle }

    context(VerificationContext)
    val keepNext: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { keepNext }

    context(VerificationContext)
    val keepLines: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { keepLines }

    context(VerificationContext)
    val pageBreakBefore: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { pageBreakBefore }

    context(VerificationContext)
    val framePr: CTFramePr?
        get() = resolver.getActualProperty(p) { framePr }

    context(VerificationContext)
    val widowControl: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { widowControl }

    context(VerificationContext)
    val numPr: NumPr?
        get() = resolver.getActualProperty(p) { numPr }

    context(VerificationContext)
    val suppressLineNumbers: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { suppressLineNumbers }

    context(VerificationContext)
    val pBdr: PBdr?
        get() = resolver.getActualProperty(p) { pBdr }

    context(VerificationContext)
    val shd: CTShd?
        get() = resolver.getActualProperty(p) { shd }

    context(VerificationContext)
    val tabs: Tabs?
        get() = resolver.getActualProperty(p) { tabs }

    context(VerificationContext)
    val suppressAutoHyphens: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { suppressAutoHyphens }

    context(VerificationContext)
    val kinsoku: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { kinsoku }

    context(VerificationContext)
    val wordWrap: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { wordWrap }

    context(VerificationContext)
    val overflowPunct: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { overflowPunct }

    context(VerificationContext)
    val topLinePunct: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { topLinePunct }

    context(VerificationContext)
    val autoSpaceDE: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { autoSpaceDE }

    context(VerificationContext)
    val autoSpaceDN: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { autoSpaceDN }

    context(VerificationContext)
    val bidi: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { bidi }

    context(VerificationContext)
    val adjustRightInd: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { adjustRightInd }

    context(VerificationContext)
    val snapToGrid: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { snapToGrid }

    context(VerificationContext)
    val spacing: Spacing?
        get() = resolver.getActualProperty(p) { spacing }

    context(VerificationContext)
    val ind: Ind?
        get() = resolver.getActualProperty(p) { ind }

    context(VerificationContext)
    val contextualSpacing: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { contextualSpacing }

    context(VerificationContext)
    val mirrorIndents: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { mirrorIndents }

    context(VerificationContext)
    val suppressOverlap: BooleanDefaultTrue?
        get() = resolver.getActualProperty(p) { suppressOverlap }

    context(VerificationContext)
    val jc: Jc?
        get() = resolver.getActualProperty(p) { jc }

    context(VerificationContext)
    val textDirection: TextDirection?
        get() = resolver.getActualProperty(p) { textDirection }

    context(VerificationContext)
    val textAlignment: TextAlignment?
        get() = resolver.getActualProperty(p) { textAlignment }

    context(VerificationContext)
    val textboxTightWrap: CTTextboxTightWrap?
        get() = resolver.getActualProperty(p) { textboxTightWrap }

    context(VerificationContext)
    val outlineLvl: OutlineLvl?
        get() = resolver.getActualProperty(p) { outlineLvl }

    context(VerificationContext)
    val divId: DivId?
        get() = resolver.getActualProperty(p) { divId }

    context(VerificationContext)
    val cnfStyle: CTCnf?
        get() = resolver.getActualProperty(p) { cnfStyle }

    context(VerificationContext)
    val resolvedNumberingStyle: Lvl?
        get() {
            val numbering = doc.numberingDefinitionsPart.jaxbElement
            val abstractNumId = numbering.num.firstOrNull { it.numId == numPr?.numId?.`val` }?.abstractNumId?.`val`
            val abstract = numbering.abstractNum.firstOrNull { it.abstractNumId == abstractNumId }
            return numPr?.ilvl?.`val`?.toInt()?.let { abstract?.lvl?.get(it) }
        }
}

context(VerificationContext)
val P.resolvedPPr: ResolvedPPr
    get() = ResolvedPPr(this)