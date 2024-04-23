package normativecontrol.core.wrappers

import normativecontrol.core.contexts.VerificationContext
import org.docx4j.w14.*
import org.docx4j.wml.*

@JvmInline
@Suppress("unused")
value class RPr(private val rPr: org.docx4j.wml.RPr?) {
    context(VerificationContext)
    val rStyle: RStyle?
        get() = resolver.getActualProperty(rPr) { rStyle }

    context(VerificationContext)
    val rFonts: RFonts
        get() = RFonts(rPr)

    context(VerificationContext)
    val b: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { b }

    context(VerificationContext)
    val bCs: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { bCs }

    context(VerificationContext)
    val i: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { i }

    context(VerificationContext)
    val iCs: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { iCs }

    context(VerificationContext)
    val caps: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { caps }

    context(VerificationContext)
    val smallCaps: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { smallCaps }

    context(VerificationContext)
    val strike: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { strike }

    context(VerificationContext)
    val dstrike: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { dstrike }

    context(VerificationContext)
    val outline: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { outline }

    context(VerificationContext)
    val shadow: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { shadow }

    context(VerificationContext)
    val emboss: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { emboss }

    context(VerificationContext)
    val imprint: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { imprint }

    context(VerificationContext)
    val noProof: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { noProof }

    context(VerificationContext)
    val snapToGrid: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { snapToGrid }

    context(VerificationContext)
    val vanish: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { vanish }

    context(VerificationContext)
    val webHidden: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { webHidden }

    context(VerificationContext)
    val color: Color?
        get() = resolver.getActualProperty(rPr) { color }

    context(VerificationContext)
    val spacing: CTSignedTwipsMeasure?
        get() = resolver.getActualProperty(rPr) { spacing }

    context(VerificationContext)
    val w: CTTextScale?
        get() = resolver.getActualProperty(rPr) { w }

    context(VerificationContext)
    val kern: HpsMeasure?
        get() = resolver.getActualProperty(rPr) { kern }

    context(VerificationContext)
    val position: CTSignedHpsMeasure?
        get() = resolver.getActualProperty(rPr) { position }

    context(VerificationContext)
    val sz: HpsMeasure?
        get() = resolver.getActualProperty(rPr) { sz }

    context(VerificationContext)
    val szCs: HpsMeasure?
        get() = resolver.getActualProperty(rPr) { szCs }

    context(VerificationContext)
    val highlight: Highlight?
        get() = resolver.getActualProperty(rPr) { highlight }

    context(VerificationContext)
    val u: U?
        get() = resolver.getActualProperty(rPr) { u }

    context(VerificationContext)
    val effect: CTTextEffect?
        get() = resolver.getActualProperty(rPr) { effect }

    context(VerificationContext)
    val bdr: CTBorder?
        get() = resolver.getActualProperty(rPr) { bdr }

    context(VerificationContext)
    val shd: CTShd?
        get() = resolver.getActualProperty(rPr) { shd }

    context(VerificationContext)
    val fitText: CTFitText?
        get() = resolver.getActualProperty(rPr) { fitText }

    context(VerificationContext)
    val vertAlign: CTVerticalAlignRun?
        get() = resolver.getActualProperty(rPr) { vertAlign }

    context(VerificationContext)
    val rtl: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { rtl }

    context(VerificationContext)
    val cs: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { cs }

    context(VerificationContext)
    val em: CTEm?
        get() = resolver.getActualProperty(rPr) { em }

    context(VerificationContext)
    val lang: CTLanguage?
        get() = resolver.getActualProperty(rPr) { lang }

    context(VerificationContext)
    val eastAsianLayout: CTEastAsianLayout?
        get() = resolver.getActualProperty(rPr) { eastAsianLayout }

    context(VerificationContext)
    val specVanish: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { specVanish }

    context(VerificationContext)
    val oMath: BooleanDefaultTrue?
        get() = resolver.getActualProperty(rPr) { oMath }

    context(VerificationContext)
    val glow: CTGlow?
        get() = resolver.getActualProperty(rPr) { glow }

    context(VerificationContext)
    val shadow14: CTShadow?
        get() = resolver.getActualProperty(rPr) { shadow14 }

    context(VerificationContext)
    val reflection: CTReflection?
        get() = resolver.getActualProperty(rPr) { reflection }

    context(VerificationContext)
    val textOutline: CTTextOutlineEffect?
        get() = resolver.getActualProperty(rPr) { textOutline }

    context(VerificationContext)
    val textFill: CTFillTextEffect?
        get() = resolver.getActualProperty(rPr) { textFill }

    context(VerificationContext)
    val scene3D: CTScene3D?
        get() = resolver.getActualProperty(rPr) { scene3D }

    context(VerificationContext)
    val props3D: CTProps3D?
        get() = resolver.getActualProperty(rPr) { props3D }

    context(VerificationContext)
    val ligatures: CTLigatures?
        get() = resolver.getActualProperty(rPr) { ligatures }

    context(VerificationContext)
    val numForm: CTNumForm?
        get() = resolver.getActualProperty(rPr) { numForm }

    context(VerificationContext)
    val numSpacing: CTNumSpacing?
        get() = resolver.getActualProperty(rPr) { numSpacing }

    context(VerificationContext)
    val stylisticSets: CTStylisticSets?
        get() = resolver.getActualProperty(rPr) { stylisticSets }

    context(VerificationContext)
    val cntxtAlts: CTOnOff?
        get() = resolver.getActualProperty(rPr) { cntxtAlts }

    context(VerificationContext)
    val rPrChange: CTRPrChange?
        get() = resolver.getActualProperty(rPr) { rPrChange }

    @JvmInline
    value class RFonts(private val rPr: org.docx4j.wml.RPr?) {
        context(VerificationContext)
        val hint: STHint?
            get() = resolver.getActualProperty(rPr) { rFonts?.hint }

        context(VerificationContext)
        val ascii: String?
            get() = resolver.getActualProperty(rPr) { rFonts?.ascii }

        context(VerificationContext)
        val hAnsi: String?
            get() = resolver.getActualProperty(rPr) { rFonts?.hAnsi }

        context(VerificationContext)
        val eastAsia: String?
            get() = resolver.getActualProperty(rPr) { rFonts?.eastAsia }

        context(VerificationContext)
        val cs: String?
            get() = resolver.getActualProperty(rPr) { rFonts?.cs }

        context(VerificationContext)
        val asciiTheme: STTheme?
            get() = resolver.getActualProperty(rPr) { rFonts?.asciiTheme }

        context(VerificationContext)
        val hAnsiTheme: STTheme?
            get() = resolver.getActualProperty(rPr) { rFonts?.hAnsiTheme }

        context(VerificationContext)
        val eastAsiaTheme: STTheme?
            get() = resolver.getActualProperty(rPr) { rFonts?.eastAsiaTheme }

        context(VerificationContext)
        val cstheme: STTheme?
            get() = resolver.getActualProperty(rPr) { rFonts?.cstheme }
    }

    companion object {
        fun org.docx4j.wml.RPr?.resolve(): RPr = RPr(this)
    }
}
