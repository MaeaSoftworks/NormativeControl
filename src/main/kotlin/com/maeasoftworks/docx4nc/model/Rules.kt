@file:Suppress("UNUSED_PARAMETER")

package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType.*
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.TextUtils
import org.docx4j.wml.JcEnumeration
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr
import kotlin.math.abs
import kotlin.math.floor

object Rules {
    object Default {
        object Common {
            object P {
                fun notBordered(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.pBdr != null && setOf(
                            pPr.pBdr.left,
                            pPr.pBdr.right,
                            pPr.pBdr.top,
                            pPr.pBdr.bottom
                        ).any { it.`val`.name != "NIL" }
                    ) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_BORDER else TEXT_COMMON_BORDER, p)
                    } else null
                }

                fun hasNotBackground(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_BACKGROUND_FILL else TEXT_COMMON_BACKGROUND_FILL, p)
                    } else null
                }
            }

            object R {
                fun isTimesNewRoman(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    if (rPr.rFonts.ascii != null) {
                        return if (rPr.rFonts.ascii != "Times New Roman") {
                            MistakeInner(
                                if (isEmpty) TEXT_WHITESPACE_FONT else TEXT_COMMON_FONT,
                                p,
                                r,
                                "${rPr.rFonts.ascii}/Times New Roman"
                            )
                        } else null
                    } else if (rPr.rFonts.asciiTheme != null) {
                        val run = rPr.parent as org.docx4j.wml.R
                        val style = if (run.rPr?.rStyle?.`val` == null) d.doc.styleDefinitionsPart.getStyleById("Normal") else d.doc.styleDefinitionsPart.getStyleById(run.rPr.rStyle.`val`)
                        return if (style?.rPr?.rFonts?.ascii != "Times New Roman") {
                            MistakeInner(
                                if (isEmpty) TEXT_WHITESPACE_FONT else TEXT_COMMON_FONT,
                                p,
                                r,
                                "${rPr.rFonts.ascii}/Times New Roman"
                            )
                        } else null
                    } else return null
                }

                fun fontSizeIs14(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    val run = rPr.parent as org.docx4j.wml.R
                    return if ((run.rPr?.rStyle == null && d.doc.styleDefinitionsPart.getStyleById("Normal").rPr?.sz?.`val`?.toInt()?.div(2) != 14) && run.rPr?.sz?.`val`?.toInt()?.div(2) != 14 && rPr.sz.`val`.toInt() / 2 != 14) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_INCORRECT_FONT_SIZE else TEXT_COMMON_INCORRECT_FONT_SIZE,
                            p,
                            r,
                            "${rPr.sz.`val`.toInt() / 2}/14"
                        )
                    } else null
                }

                fun notItalic(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (!(rPr.i == null || !rPr.i.isVal)) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_ITALIC else TEXT_COMMON_ITALIC_TEXT, p, r)
                    } else null
                }

                fun notCrossedOut(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (!(rPr.strike == null || !rPr.strike.isVal)) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_STRIKETHROUGH else TEXT_COMMON_STRIKETHROUGH, p, r)
                    } else null
                }

                fun notHighlighted(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_HIGHLIGHT else TEXT_COMMON_HIGHLIGHT, p, r)
                    } else null
                }

                fun isBlack(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (rPr.color != null && rPr.color.`val` != "000000" && rPr.color.`val` != "auto") {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_TEXT_COLOR else TEXT_COMMON_TEXT_COLOR,
                            p,
                            r,
                            "${rPr.color.`val`}/black"
                        )
                    } else null
                }

                fun letterSpacingIs0(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (rPr.spacing != null && rPr.spacing.`val` != null && rPr.spacing.`val`.toDouble() != 0.0) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_RUN_SPACING else TEXT_COMMON_RUN_SPACING,
                            p,
                            r,
                            "${rPr.spacing.`val`.toDouble()}/0"
                        )
                    } else null
                }
            }
        }

        object Header {
            object P {
                fun justifyIsCenter(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_ALIGNMENT,
                            p,
                            description = "${pPr.jc?.`val`}/${JcEnumeration.CENTER}"
                        )
                    } else null
                }

                fun lineSpacingIsOne(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.spacing != null && pPr.spacing.line != null &&
                        pPr.spacing.line.toDouble() != 240.0
                    ) {
                        MistakeInner(
                            TEXT_HEADER_LINE_SPACING,
                            p,
                            description = "${pPr.spacing.line.toDouble() / 240.0}/1"
                        )
                    } else null
                }

                fun hasNotDotInEnd(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    val text = TextUtils.getText(d.doc.content[p] as org.docx4j.wml.P)
                    return if (text.endsWith(".")) {
                        MistakeInner(TEXT_HEADER_REDUNDANT_DOT, p)
                    } else null
                }

                fun emptyLineAfterHeaderExists(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    if (d.doc.content.size <= p + 1) {
                        return MistakeInner(CHAPTER_EMPTY, p + 1)
                    }
                    val isNotEmpty = try {
                        TextUtils.getText(d.doc.content[p + 1] as org.docx4j.wml.P).isNotBlank()
                    } catch (e: ClassCastException) {
                        return MistakeInner(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, p)
                    }
                    return if (isNotEmpty) {
                        MistakeInner(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, p)
                    } else null
                }

                fun firstLineIndentIs1dot25(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.numPr != null && pPr.ind != null && pPr.ind.firstLine != null &&
                        abs(floor(pPr.ind.firstLine.toDouble() / 1440.0 * 2.54) - 1.25) <= 0.01
                    ) {
                        MistakeInner(
                            TEXT_HEADER_INDENT_FIRST_LINES, p,
                            description = "${floor(pPr.ind.firstLine.toDouble() / 1440.0 * 2.54)}/1.25"
                        )
                    } else null
                }

                fun isAutoHyphenSuppressed(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.suppressAutoHyphens == null || !pPr.suppressAutoHyphens.isVal) {
                        MistakeInner(TEXT_HEADER_AUTO_HYPHEN, p)
                    } else null
                }
            }

            object R {
                fun isUppercase(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    val text = TextUtils.getText((d.doc.content[p] as org.docx4j.wml.P).content[r] as org.docx4j.wml.R)
                    return if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_UPPERCASE else TEXT_HEADER_NOT_UPPERCASE,
                            p,
                            r
                        )
                    } else null
                }

                fun isBold(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (rPr.b == null || !rPr.b.isVal) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_BOLD else TEXT_HEADER_NOT_BOLD, p, r)
                    } else null
                }
            }
        }

        object RegularText {
            object P {
                fun justifyIsBoth(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_ALIGNMENT else TEXT_REGULAR_INCORRECT_ALIGNMENT,
                            p,
                            description = "${pPr.jc?.`val`}/${JcEnumeration.BOTH}"
                        )
                    } else null
                }

                fun lineSpacingIsOneAndHalf(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.spacing != null && pPr.spacing.line != null) {
                        if (pPr.spacing.lineRule.value() == "auto" && pPr.spacing.line.toDouble() != 360.0) {
                            MistakeInner(
                                if (isEmpty) TEXT_WHITESPACE_LINE_SPACING else TEXT_REGULAR_LINE_SPACING,
                                p,
                                description = "${pPr.spacing.line.toDouble() / 240.0}/1.5"
                            )
                        } else null
                    } else null
                }

                fun firstLineIndentIs1dot25(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.numPr != null && pPr.ind != null &&
                        pPr.ind.firstLine != null &&
                        abs(floor(pPr.ind.firstLine.toDouble() / 1440.0 * 2.54) - 1.25) <= 0.01
                    ) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_INDENT_FIRST_LINES else TEXT_REGULAR_INDENT_FIRST_LINES,
                            p,
                            description = "${floor(pPr.ind.firstLine.toDouble() / 1440 * 2.54)}/1.25"
                        )
                    } else null
                }

                fun leftIndentIs0(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.numPr != null && pPr.ind != null &&
                        pPr.ind.left != null && pPr.ind.left.toDouble() != 0.0
                    ) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_INDENT_LEFT else TEXT_COMMON_INDENT_LEFT,
                            p,
                            description = "${pPr.ind.left.toDouble() / 240.0}/0"
                        )
                    } else null
                }

                fun rightIndentIs0(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.numPr != null && pPr.ind != null &&
                        pPr.ind.right != null && pPr.ind.right.toDouble() != 0.0
                    ) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_INDENT_RIGHT else TEXT_COMMON_INDENT_RIGHT,
                            p,
                            description = "${pPr.ind.right.toDouble() / 240.0}/0"
                        )
                    } else null
                }
            }

            object R {
                fun isNotBold(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (rPr.b != null && !rPr.b.isVal) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_BOLD else TEXT_REGULAR_WAS_BOLD, p, r)
                    } else null
                }

                fun isNotCaps(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (rPr.caps != null && !rPr.caps.isVal) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_UPPERCASE else TEXT_REGULAR_UPPERCASE, p, r)
                    } else null
                }

                fun isUnderline(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (rPr.u != null && rPr.u.`val`.value() != "none") {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_UNDERLINED else TEXT_COMMON_UNDERLINED, p, r)
                    } else null
                }
            }
        }

        object PictureTitle {
            object P {
                fun justifyIsCenter(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else PICTURE_TITLE_NOT_CENTERED,
                            p,
                            description = "${pPr.jc.`val`}/${JcEnumeration.CENTER}"
                        )
                    } else null
                }

                fun hasNotDotInEnd(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    val text = TextUtils.getText(d.doc.content[p] as org.docx4j.wml.P)
                    return if (text.endsWith(".")) {
                        MistakeInner(PICTURE_TITLE_ENDS_WITH_DOT, p)
                    } else null
                }
            }
        }
    }

    object Body {
        object Header {
            object P {
                fun justifyIsLeft(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    return if (pPr.jc != null && pPr.jc.`val` != JcEnumeration.LEFT) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_BODY_ALIGNMENT,
                            p, description = "${pPr.jc.`val`}/${JcEnumeration.LEFT}"
                        )
                    } else null
                }

                fun isNotUppercase(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                    val paragraph = d.doc.content[p] as org.docx4j.wml.P
                    val text = TextUtils.getText(paragraph)
                    return if (!isEmpty && (
                                text.uppercase() == text || (
                                        paragraph.content.all { x ->
                                            if (x is R) {
                                                val rPr = d.doc.propertyResolver.getEffectiveRPr(x.rPr, pPr)
                                                rPr.caps != null && rPr.caps.isVal
                                            } else {
                                                true
                                            }
                                        }
                                        )
                                )
                    ) {
                        MistakeInner(TEXT_HEADER_BODY_UPPERCASE, p)
                    } else null
                }
            }
        }
    }

    object List {
        object P {
            fun justifyIsCenter(p: Int, pPr: PPr, isEmpty: Boolean, d: DocumentParser): MistakeInner? {
                return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                    MistakeInner(
                        if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else PICTURE_TITLE_NOT_CENTERED,
                        p,
                        description = "${pPr.jc.`val`}/${JcEnumeration.CENTER}"
                    )
                } else null
            }
        }
    }
}
