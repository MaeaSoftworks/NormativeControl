package com.maeasoftworks.polonium.model

import com.maeasoftworks.polonium.enums.MistakeType.*
import com.maeasoftworks.polonium.utils.PFunction
import com.maeasoftworks.polonium.utils.RFunction
import org.docx4j.TextUtils
import org.docx4j.wml.JcEnumeration
import org.docx4j.wml.R
import kotlin.math.abs
import kotlin.math.floor

/**
 * Set of rules for all cases.
 *
 * One function - one rule.
 *
 * Every function's signature for paragraph rule must be equal to
 * [PFunction][com.maeasoftworks.polonium.utils.PFunction]
 * or
 * [RFunction][com.maeasoftworks.polonium.utils.RFunction]
 * for run rule.
 */
object Rules {
    object Default {
        object Common {
            object P {
                val notBordered: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.pBdr != null && setOf(
                            pPr.pBdr.left,
                            pPr.pBdr.right,
                            pPr.pBdr.top,
                            pPr.pBdr.bottom
                        ).any { it.`val`.name != "NIL" }
                    ) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_BORDER else TEXT_COMMON_BORDER, p)
                    } else null
                }

                val hasNotBackground: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_BACKGROUND_FILL else TEXT_COMMON_BACKGROUND_FILL, p)
                    } else null
                }
            }

            object R {
                val isTimesNewRoman: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (rPr.rFonts?.ascii != "Times New Roman") {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_FONT else TEXT_COMMON_FONT,
                            p,
                            r,
                            "${rPr.rFonts?.ascii}/Times New Roman"
                        )
                    } else null
                }

                val fontSizeIs14: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (rPr.sz.`val`.toInt() / 2 != 14) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_INCORRECT_FONT_SIZE else TEXT_COMMON_INCORRECT_FONT_SIZE,
                            p,
                            r,
                            "${rPr.sz.`val`.toInt() / 2}/14"
                        )
                    } else null
                }

                val notItalic: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (!(rPr.i == null || !rPr.i.isVal)) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_ITALIC else TEXT_COMMON_ITALIC_TEXT, p, r)
                    } else null
                }

                val notCrossedOut: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (!(rPr.strike == null || !rPr.strike.isVal)) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_STRIKETHROUGH else TEXT_COMMON_STRIKETHROUGH, p, r)
                    } else null
                }

                val notHighlighted: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_HIGHLIGHT else TEXT_COMMON_HIGHLIGHT, p, r)
                    } else null
                }

                val isBlack: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (rPr.color != null && rPr.color.`val` != "000000" && rPr.color.`val` != "auto") {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_TEXT_COLOR else TEXT_COMMON_TEXT_COLOR,
                            p,
                            r,
                            "${rPr.color.`val`}/black"
                        )
                    } else null
                }

                val letterSpacingIs0: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (rPr.spacing != null && rPr.spacing.`val` != null && rPr.spacing.`val`.toDouble() != 0.0) {
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
                val justifyIsCenter: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_ALIGNMENT,
                            p,
                            description = "${pPr.jc?.`val`}/${JcEnumeration.CENTER}"
                        )
                    } else null
                }

                val lineSpacingIsOne: PFunction = { p, pPr, _, _ ->
                    if (pPr.spacing != null && pPr.spacing.line != null && pPr.spacing.line.toDouble() != 240.0) {
                        MistakeInner(
                            TEXT_HEADER_LINE_SPACING,
                            p,
                            description = "${pPr.spacing.line.toDouble() / 240.0}/1"
                        )
                    } else null
                }

                val hasNotDotInEnd: PFunction = { p, _, _, d ->
                    if (TextUtils.getText(d.doc.content[p] as org.docx4j.wml.P).endsWith(".")) {
                        MistakeInner(TEXT_HEADER_REDUNDANT_DOT, p)
                    } else null
                }

                val emptyLineAfterHeaderExists: PFunction = { p, _, _, d ->
                    if (d.doc.content.size <= p + 1) MistakeInner(CHAPTER_EMPTY, p + 1)
                    var caught: MistakeInner? = null
                    val isNotEmpty = try {
                        TextUtils.getText(d.doc.content[p + 1] as org.docx4j.wml.P).isNotBlank()
                    } catch (e: ClassCastException) {
                        caught = MistakeInner(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, p)
                        false
                    }
                    caught ?: if (isNotEmpty) MistakeInner(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, p) else null
                }

                val firstLineIndentIs1dot25: PFunction = { p, pPr, _, _ ->
                    if (pPr.numPr != null && pPr.ind != null && pPr.ind.firstLine != null &&
                        abs(floor(pPr.ind.firstLine.toDouble() / 1440.0 * 2.54) - 1.25) <= 0.01
                    ) {
                        MistakeInner(
                            TEXT_HEADER_INDENT_FIRST_LINES,
                            p,
                            description = "${floor(pPr.ind.firstLine.toDouble() / 1440.0 * 2.54)}/1.25"
                        )
                    } else null
                }

                val isAutoHyphenSuppressed: PFunction = { p, pPr, _, d ->
                    if ((pPr.suppressAutoHyphens == null || !pPr.suppressAutoHyphens.isVal) && d.autoHyphenation == true) {
                        MistakeInner(TEXT_HEADER_AUTO_HYPHEN, p)
                    } else null
                }
            }

            object R {
                val isUppercase: RFunction = { p, r, rPr, isEmpty, d ->
                    val text = TextUtils.getText((d.doc.content[p] as org.docx4j.wml.P).content[r] as org.docx4j.wml.R)
                    if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_UPPERCASE else TEXT_HEADER_NOT_UPPERCASE,
                            p,
                            r
                        )
                    } else null
                }

                val isBold: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (rPr.b == null || !rPr.b.isVal) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_BOLD else TEXT_HEADER_NOT_BOLD, p, r)
                    } else null
                }
            }
        }

        object RegularText {
            object P {
                val justifyIsBoth: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_ALIGNMENT else TEXT_REGULAR_INCORRECT_ALIGNMENT,
                            p,
                            description = "${pPr.jc?.`val`}/${JcEnumeration.BOTH}"
                        )
                    } else null
                }

                val lineSpacingIsOneAndHalf: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.spacing != null && pPr.spacing.line != null) {
                        if (pPr.spacing.lineRule.value() == "auto" && pPr.spacing.line.toDouble() != 360.0) {
                            MistakeInner(
                                if (isEmpty) TEXT_WHITESPACE_LINE_SPACING else TEXT_REGULAR_LINE_SPACING,
                                p,
                                description = "${pPr.spacing.line.toDouble() / 240.0}/1.5"
                            )
                        } else null
                    } else null
                }

                val firstLineIndentIs1dot25: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.numPr != null && pPr.ind != null &&
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

                val leftIndentIs0: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.numPr != null && pPr.ind != null &&
                        pPr.ind.left != null && pPr.ind.left.toDouble() != 0.0
                    ) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_INDENT_LEFT else TEXT_COMMON_INDENT_LEFT,
                            p,
                            description = "${pPr.ind.left.toDouble() / 240.0}/0"
                        )
                    } else null
                }

                val rightIndentIs0: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.numPr != null && pPr.ind != null &&
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
                val isNotBold: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (rPr.b != null && !rPr.b.isVal) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_BOLD else TEXT_REGULAR_WAS_BOLD, p, r)
                    } else null
                }

                val isNotCaps: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (rPr.caps != null && !rPr.caps.isVal) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_UPPERCASE else TEXT_REGULAR_UPPERCASE, p, r)
                    } else null
                }

                val isUnderline: RFunction = { p, r, rPr, isEmpty, _ ->
                    if (rPr.u != null && rPr.u.`val`.value() != "none") {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_UNDERLINED else TEXT_COMMON_UNDERLINED, p, r)
                    } else null
                }
            }
        }

        object PictureTitle {
            object P {
                val justifyIsCenter: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else PICTURE_TITLE_NOT_CENTERED,
                            p,
                            description = "${pPr.jc.`val`}/${JcEnumeration.CENTER}"
                        )
                    } else null
                }

                val hasNotDotInEnd: PFunction = { p, _, _, d ->
                    if (TextUtils.getText(d.doc.content[p] as org.docx4j.wml.P).endsWith(".")) {
                        MistakeInner(PICTURE_TITLE_ENDS_WITH_DOT, p)
                    } else null
                }
            }
        }
    }

    object Body {
        object Header {
            object P {
                val justifyIsLeft: PFunction = { p, pPr, isEmpty, _ ->
                    if (pPr.jc != null && pPr.jc.`val` != JcEnumeration.LEFT) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_BODY_ALIGNMENT,
                            p,
                            description = "${pPr.jc.`val`}/${JcEnumeration.LEFT}"
                        )
                    } else null
                }

                val isNotUppercase: PFunction = { p, pPr, isEmpty, d ->
                    val paragraph = d.doc.content[p] as org.docx4j.wml.P
                    val text = TextUtils.getText(paragraph)
                    if (!isEmpty && (
                        text.uppercase() == text || (
                            paragraph.content.all { x ->
                                if (x is R) {
                                    val rPr = d.doc.propertyResolver.getEffectiveRPr(x.rPr, pPr)
                                    rPr.caps != null && rPr.caps.isVal
                                } else true
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
}
