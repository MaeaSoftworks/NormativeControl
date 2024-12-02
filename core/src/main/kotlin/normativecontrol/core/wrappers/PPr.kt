package normativecontrol.core.wrappers

import normativecontrol.core.contexts.VerificationContext
import org.docx4j.wml.*
import org.docx4j.wml.PPrBase.*
import java.math.BigInteger

/**
 * Redirection object for resolving PPr styles.
 * @param pPr target PPr
 */
@JvmInline
@Suppress("unused")
value class PPr(private val pPr: org.docx4j.wml.PPr?) {
    context(VerificationContext)
    val pStyle: PStyle?
        get() = resolver.getActualProperty(pPr) { pStyle }

    context(VerificationContext)
    val keepNext: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { keepNext }

    context(VerificationContext)
    val keepLines: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { keepLines }

    context(VerificationContext)
    val pageBreakBefore: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { pageBreakBefore }

    context(VerificationContext)
    val framePr: CTFramePr?
        get() = resolver.getActualProperty(pPr) { framePr }

    context(VerificationContext)
    val widowControl: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { widowControl }

    context(VerificationContext)
    val numPr: NumPr?
        get() = resolver.getActualProperty(pPr) { numPr }

    context(VerificationContext)
    val suppressLineNumbers: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { suppressLineNumbers }

    context(VerificationContext)
    val pBdr: PBdr?
        get() = resolver.getActualProperty(pPr) { pBdr }

    context(VerificationContext)
    val shd: Shd
        get() = Shd(pPr)

    context(VerificationContext)
    val tabs: Tabs?
        get() = resolver.getActualProperty(pPr) { tabs }

    context(VerificationContext)
    val suppressAutoHyphens: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { suppressAutoHyphens }

    context(VerificationContext)
    val kinsoku: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { kinsoku }

    context(VerificationContext)
    val wordWrap: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { wordWrap }

    context(VerificationContext)
    val overflowPunct: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { overflowPunct }

    context(VerificationContext)
    val topLinePunct: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { topLinePunct }

    context(VerificationContext)
    val autoSpaceDE: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { autoSpaceDE }

    context(VerificationContext)
    val autoSpaceDN: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { autoSpaceDN }

    context(VerificationContext)
    val bidi: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { bidi }

    context(VerificationContext)
    val adjustRightInd: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { adjustRightInd }

    context(VerificationContext)
    val snapToGrid: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { snapToGrid }

    context(VerificationContext)
    val spacing: Spacing
        get() = Spacing(pPr)

    context(VerificationContext)
    val ind: Ind
        get() = Ind(pPr)

    context(VerificationContext)
    val contextualSpacing: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { contextualSpacing }

    context(VerificationContext)
    val mirrorIndents: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { mirrorIndents }

    context(VerificationContext)
    val suppressOverlap: BooleanDefaultTrue?
        get() = resolver.getActualProperty(pPr) { suppressOverlap }

    context(VerificationContext)
    val jc: Jc?
        get() = resolver.getActualProperty(pPr) { jc }

    context(VerificationContext)
    val textDirection: TextDirection?
        get() = resolver.getActualProperty(pPr) { textDirection }

    context(VerificationContext)
    val textAlignment: TextAlignment?
        get() = resolver.getActualProperty(pPr) { textAlignment }

    context(VerificationContext)
    val textboxTightWrap: CTTextboxTightWrap?
        get() = resolver.getActualProperty(pPr) { textboxTightWrap }

    context(VerificationContext)
    val outlineLvl: OutlineLvl?
        get() = resolver.getActualProperty(pPr) { outlineLvl }

    context(VerificationContext)
    val divId: DivId?
        get() = resolver.getActualProperty(pPr) { divId }

    context(VerificationContext)
    val cnfStyle: CTCnf?
        get() = resolver.getActualProperty(pPr) { cnfStyle }

    context(VerificationContext)
    val numberingStyle: Lvl?
        get() = resolver.resolveNumberingStyle(pPr)

    @JvmInline
    value class Ind(private val pPr: org.docx4j.wml.PPr?) {
        context(VerificationContext)
        val left: BigInteger?
            get() = resolver.getActualProperty(pPr) { ind?.left }

        context(VerificationContext)
        val leftChars: BigInteger?
            get() = resolver.getActualProperty(pPr) { ind?.leftChars }

        context(VerificationContext)
        val right: BigInteger?
            get() = resolver.getActualProperty(pPr) { ind?.right }

        context(VerificationContext)
        val rightChars: BigInteger?
            get() = resolver.getActualProperty(pPr) { ind?.rightChars }

        context(VerificationContext)
        val hanging: BigInteger?
            get() = resolver.getActualProperty(pPr) { ind?.hanging }

        context(VerificationContext)
        val hangingChars: BigInteger?
            get() = resolver.getActualProperty(pPr) { ind?.hangingChars }

        context(VerificationContext)
        val firstLine: BigInteger?
            get() = resolver.getActualProperty(pPr) { ind?.firstLine }

        context(VerificationContext)
        val firstLineChars: BigInteger?
            get() = resolver.getActualProperty(pPr) { ind?.firstLineChars }
    }

    @JvmInline
    value class Shd(private val pPr: org.docx4j.wml.PPr?) {
        context(VerificationContext)
        val `val`: STShd?
            get() = resolver.getActualProperty(pPr) { shd?.`val` }

        context(VerificationContext)
        val color: String?
            get() = resolver.getActualProperty(pPr) { shd?.color }

        context(VerificationContext)
        val themeColor: STThemeColor?
            get() = resolver.getActualProperty(pPr) { shd?.themeColor }

        context(VerificationContext)
        val themeTint: String?
            get() = resolver.getActualProperty(pPr) { shd?.themeTint }

        context(VerificationContext)
        val themeShade: String?
            get() = resolver.getActualProperty(pPr) { shd?.themeShade }

        context(VerificationContext)
        val fill: String?
            get() = resolver.getActualProperty(pPr) { shd?.fill }

        context(VerificationContext)
        val themeFill: STThemeColor?
            get() = resolver.getActualProperty(pPr) { shd?.themeFill }

        context(VerificationContext)
        val themeFillTint: String?
            get() = resolver.getActualProperty(pPr) { shd?.themeFillTint }

        context(VerificationContext)
        val themeFillShade: String?
            get() = resolver.getActualProperty(pPr) { shd?.themeFillShade }
    }

    @JvmInline
    value class Spacing(private val pPr: org.docx4j.wml.PPr?) {
        context(VerificationContext)
        val before: BigInteger?
            get() = resolver.getActualProperty(pPr) { spacing?.before }

        context(VerificationContext)
        val beforeLines: BigInteger?
            get() = resolver.getActualProperty(pPr) { spacing?.beforeLines }

        context(VerificationContext)
        val after: BigInteger?
            get() = resolver.getActualProperty(pPr) { spacing?.after }

        context(VerificationContext)
        val afterLines: BigInteger?
            get() = resolver.getActualProperty(pPr) { spacing?.afterLines }

        context(VerificationContext)
        val line: BigInteger?
            get() = resolver.getActualProperty(pPr) { spacing?.line }

        context(VerificationContext)
        val lineRule: STLineSpacingRule?
            get() = resolver.getActualProperty(pPr) { spacing?.lineRule }
    }

    companion object {
        fun org.docx4j.wml.PPr?.resolve() = PPr(this)
    }
}