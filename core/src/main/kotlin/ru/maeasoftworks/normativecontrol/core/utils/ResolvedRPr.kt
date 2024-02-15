package ru.maeasoftworks.normativecontrol.core.utils

import org.docx4j.w14.*
import org.docx4j.wml.*
import ru.maeasoftworks.normativecontrol.core.contexts.VerificationContext

@JvmInline
@Suppress("unused")
value class ResolvedRPr(private val r: R) {
    context(VerificationContext)
    val rStyle: RStyle?
        get() = resolver.getActualProperty(r) { rStyle }

    context(VerificationContext)
    val rFonts: ResolvedRFonts
        get() = ResolvedRFonts(r)

    context(VerificationContext)
    val b: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { b }

    context(VerificationContext)
    val bCs: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { bCs }

    context(VerificationContext)
    val i: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { i }

    context(VerificationContext)
    val iCs: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { iCs }

    context(VerificationContext)
    val caps: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { caps }

    context(VerificationContext)
    val smallCaps: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { smallCaps }

    context(VerificationContext)
    val strike: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { strike }

    context(VerificationContext)
    val dstrike: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { dstrike }

    context(VerificationContext)
    val outline: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { outline }

    context(VerificationContext)
    val shadow: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { shadow }

    context(VerificationContext)
    val emboss: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { emboss }

    context(VerificationContext)
    val imprint: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { imprint }

    context(VerificationContext)
    val noProof: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { noProof }

    context(VerificationContext)
    val snapToGrid: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { snapToGrid }

    context(VerificationContext)
    val vanish: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { vanish }

    context(VerificationContext)
    val webHidden: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { webHidden }

    context(VerificationContext)
    val color: Color?
        get() = resolver.getActualProperty(r) { color }

    context(VerificationContext)
    val spacing: CTSignedTwipsMeasure?
        get() = resolver.getActualProperty(r) { spacing }

    context(VerificationContext)
    val w: CTTextScale?
        get() = resolver.getActualProperty(r) { w }

    context(VerificationContext)
    val kern: HpsMeasure?
        get() = resolver.getActualProperty(r) { kern }

    context(VerificationContext)
    val position: CTSignedHpsMeasure?
        get() = resolver.getActualProperty(r) { position }

    context(VerificationContext)
    val sz: HpsMeasure?
        get() = resolver.getActualProperty(r) { sz }

    context(VerificationContext)
    val szCs: HpsMeasure?
        get() = resolver.getActualProperty(r) { szCs }

    context(VerificationContext)
    val highlight: Highlight?
        get() = resolver.getActualProperty(r) { highlight }

    context(VerificationContext)
    val u: U?
        get() = resolver.getActualProperty(r) { u }

    context(VerificationContext)
    val effect: CTTextEffect?
        get() = resolver.getActualProperty(r) { effect }

    context(VerificationContext)
    val bdr: CTBorder?
        get() = resolver.getActualProperty(r) { bdr }

    context(VerificationContext)
    val shd: CTShd?
        get() = resolver.getActualProperty(r) { shd }

    context(VerificationContext)
    val fitText: CTFitText?
        get() = resolver.getActualProperty(r) { fitText }

    context(VerificationContext)
    val vertAlign: CTVerticalAlignRun?
        get() = resolver.getActualProperty(r) { vertAlign }

    context(VerificationContext)
    val rtl: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { rtl }

    context(VerificationContext)
    val cs: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { cs }

    context(VerificationContext)
    val em: CTEm?
        get() = resolver.getActualProperty(r) { em }

    context(VerificationContext)
    val lang: CTLanguage?
        get() = resolver.getActualProperty(r) { lang }

    context(VerificationContext)
    val eastAsianLayout: CTEastAsianLayout?
        get() = resolver.getActualProperty(r) { eastAsianLayout }

    context(VerificationContext)
    val specVanish: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { specVanish }

    context(VerificationContext)
    val oMath: BooleanDefaultTrue?
        get() = resolver.getActualProperty(r) { oMath }

    context(VerificationContext)
    val glow: CTGlow?
        get() = resolver.getActualProperty(r) { glow }

    context(VerificationContext)
    val shadow14: CTShadow?
        get() = resolver.getActualProperty(r) { shadow14 }

    context(VerificationContext)
    val reflection: CTReflection?
        get() = resolver.getActualProperty(r) { reflection }

    context(VerificationContext)
    val textOutline: CTTextOutlineEffect?
        get() = resolver.getActualProperty(r) { textOutline }

    context(VerificationContext)
    val textFill: CTFillTextEffect?
        get() = resolver.getActualProperty(r) { textFill }

    context(VerificationContext)
    val scene3D: CTScene3D?
        get() = resolver.getActualProperty(r) { scene3D }

    context(VerificationContext)
    val props3D: CTProps3D?
        get() = resolver.getActualProperty(r) { props3D }

    context(VerificationContext)
    val ligatures: CTLigatures?
        get() = resolver.getActualProperty(r) { ligatures }

    context(VerificationContext)
    val numForm: CTNumForm?
        get() = resolver.getActualProperty(r) { numForm }

    context(VerificationContext)
    val numSpacing: CTNumSpacing?
        get() = resolver.getActualProperty(r) { numSpacing }

    context(VerificationContext)
    val stylisticSets: CTStylisticSets?
        get() = resolver.getActualProperty(r) { stylisticSets }

    context(VerificationContext)
    val cntxtAlts: CTOnOff?
        get() = resolver.getActualProperty(r) { cntxtAlts }

    context(VerificationContext)
    val rPrChange: CTRPrChange?
        get() = resolver.getActualProperty(r) { rPrChange }

    @JvmInline
    value class ResolvedRFonts(private val r: R) {
        context(VerificationContext)
        val hint: STHint?
            get() = resolver.getActualProperty(r) { rFonts?.hint }

        context(VerificationContext)
        val ascii: String?
            get() = resolver.getActualProperty(r) { rFonts?.ascii }

        context(VerificationContext)
        val hAnsi: String?
            get() = resolver.getActualProperty(r) { rFonts?.hAnsi }

        context(VerificationContext)
        val eastAsia: String?
            get() = resolver.getActualProperty(r) { rFonts?.eastAsia }

        context(VerificationContext)
        val cs: String?
            get() = resolver.getActualProperty(r) { rFonts?.cs }

        context(VerificationContext)
        val asciiTheme: STTheme?
            get() = resolver.getActualProperty(r) { rFonts?.asciiTheme }

        context(VerificationContext)
        val hAnsiTheme: STTheme?
            get() = resolver.getActualProperty(r) { rFonts?.hAnsiTheme }

        context(VerificationContext)
        val eastAsiaTheme: STTheme?
            get() = resolver.getActualProperty(r) { rFonts?.eastAsiaTheme }

        context(VerificationContext)
        val cstheme: STTheme?
            get() = resolver.getActualProperty(r) { rFonts?.cstheme }
    }
}

context(VerificationContext)
val R.resolvedRPr: ResolvedRPr
    get() = ResolvedRPr(this)