@file:Suppress("UNUSED_PARAMETER")

package com.maeasoftworks.docx4nc

import com.maeasoftworks.docx4nc.enums.MistakeType.*
import com.maeasoftworks.docx4nc.model.MistakeBody
import org.docx4j.TextUtils
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
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
                fun notBordered(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.pBdr != null) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_BORDER else TEXT_COMMON_BORDER, p)
                    } else null
                }

                fun hasNotBackground(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_BACKGROUND_FILL else TEXT_COMMON_BACKGROUND_FILL, p)
                    } else null
                }
            }

            object R {
                fun isTimesNewRoman(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (rPr.rFonts.ascii != "Times New Roman") {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_FONT else TEXT_COMMON_FONT, p, r)
                    } else null
                }

                fun fontSizeIs14(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (rPr.sz.`val`.toInt() / 2 != 14) {
                        MistakeBody(
                            if (isEmpty) TEXT_WHITESPACE_INCORRECT_FONT_SIZE else TEXT_COMMON_INCORRECT_FONT_SIZE,
                            p,
                            r
                        )
                    } else null
                }

                fun notItalic(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (!(rPr.i == null || !rPr.i.isVal)) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_ITALIC else TEXT_COMMON_ITALIC_TEXT, p, r)
                    } else null
                }

                fun notCrossedOut(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (!(rPr.strike == null || !rPr.strike.isVal)) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_STRIKETHROUGH else TEXT_COMMON_STRIKETHROUGH, p, r)
                    } else null
                }

                fun notHighlighted(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_HIGHLIGHT else TEXT_COMMON_HIGHLIGHT, p, r)
                    } else null
                }

                fun isBlack(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (rPr.color != null && rPr.color.`val` != "000000" && rPr.color.`val` != "auto") {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_TEXT_COLOR else TEXT_COMMON_TEXT_COLOR, p, r)
                    } else null
                }

                fun letterSpacingIs0(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (rPr.spacing != null && rPr.spacing.`val` != null &&
                        rPr.spacing.`val`.intValueExact() != 0
                    ) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_RUN_SPACING else TEXT_COMMON_RUN_SPACING, p, r)
                    } else null
                }
            }
        }

        object Header {
            object P {
                fun justifyIsCenter(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        MistakeBody(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_ALIGNMENT,
                            p,
                            0
                        )
                    } else null
                }

                fun lineSpacingIsOne(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.spacing != null && pPr.spacing.line != null &&
                        pPr.spacing.line.intValueExact() != 240
                    ) {
                        MistakeBody(TEXT_HEADER_LINE_SPACING, p)
                    } else null
                }

                fun hasNotDotInEnd(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    val text = TextUtils.getText(m.content[p] as org.docx4j.wml.P)
                    return if (text.endsWith(".")) {
                        MistakeBody(TEXT_HEADER_REDUNDANT_DOT, p)
                    } else null
                }

                fun emptyLineAfterHeaderExists(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    if (m.content.size <= p + 1) {
                        return MistakeBody(CHAPTER_EMPTY, p + 1)
                    }
                    val isNotEmpty = try {
                        TextUtils.getText(m.content[p + 1] as org.docx4j.wml.P).isNotEmpty()
                    } catch (e: ClassCastException) {
                        return MistakeBody(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, p + 1)
                    }
                    return if (isNotEmpty) {
                        MistakeBody(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, p + 1)
                    } else null
                }
            }

            object R {
                fun isUppercase(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    val text = TextUtils.getText((m.content[p] as org.docx4j.wml.P).content[r] as org.docx4j.wml.R)
                    return if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
                        MistakeBody(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_UPPERCASE else TEXT_HEADER_NOT_UPPERCASE,
                            p,
                            r
                        )
                    } else null
                }

                fun isBold(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (rPr.b == null || !rPr.b.isVal) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_BOLD else TEXT_HEADER_NOT_BOLD, p, r)
                    } else null
                }
            }
        }

        object RegularText {
            object P {
                fun justifyIsBoth(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_ALIGNMENT else TEXT_REGULAR_INCORRECT_ALIGNMENT, p)
                    } else null
                }

                fun lineSpacingIsOneAndHalf(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.spacing != null && pPr.spacing.line != null) {
                        if (pPr.spacing.lineRule.value() == "auto" && pPr.spacing.line.intValueExact() != 360) {
                            MistakeBody(if (isEmpty) TEXT_WHITESPACE_LINE_SPACING else TEXT_REGULAR_LINE_SPACING, p)
                        } else null
                    } else null
                }

                fun firstLineIndentIs1dot25(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.numPr != null && pPr.ind != null &&
                        pPr.ind.firstLine != null &&
                        abs(floor(pPr.ind.firstLine.intValueExact() / 1440 * 2.54) - 1.25) <= 0.01
                    ) {
                        MistakeBody(
                            if (isEmpty) TEXT_WHITESPACE_INDENT_FIRST_LINES else TEXT_COMMON_INDENT_FIRST_LINES,
                            p
                        )
                    } else null
                }

                fun leftIndentIs0(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.numPr != null && pPr.ind != null &&
                        pPr.ind.left != null && pPr.ind.left.intValueExact() != 0
                    ) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_INDENT_LEFT else TEXT_COMMON_INDENT_LEFT, p)
                    } else null
                }

                fun rightIndentIs0(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.numPr != null && pPr.ind != null &&
                        pPr.ind.right != null && pPr.ind.right.intValueExact() != 0
                    ) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_INDENT_RIGHT else TEXT_COMMON_INDENT_RIGHT, p)
                    } else null
                }
            }

            object R {
                fun isNotBold(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (rPr.b != null && !rPr.b.isVal) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_BOLD else TEXT_REGULAR_WAS_BOLD, p, r)
                    } else null
                }

                fun isNotCaps(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (rPr.caps != null && !rPr.caps.isVal) {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_UPPERCASE else TEXT_REGULAR_UPPERCASE, p, r)
                    } else null
                }

                fun isUnderline(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (rPr.u != null && rPr.u.`val`.value() != "none") {
                        MistakeBody(if (isEmpty) TEXT_WHITESPACE_UNDERLINED else TEXT_COMMON_UNDERLINED, p, r)
                    } else null
                }
            }
        }

        object PictureTitle {
            object P {
                fun justifyIsCenter(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        MistakeBody(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else PICTURE_TITLE_NOT_CENTERED,
                            p
                        )
                    } else null
                }

                fun hasNotDotInEnd(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    val text = TextUtils.getText(m.content[p] as org.docx4j.wml.P)
                    return if (text.endsWith(".")) {
                        MistakeBody(PICTURE_TITLE_ENDS_WITH_DOT, p)
                    } else null
                }
            }
        }
    }

    object Body {
        object Header {
            object P {
                fun justifyIsLeft(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    return if (pPr.jc != null && pPr.jc.`val` != JcEnumeration.LEFT) {
                        MistakeBody(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_BODY_ALIGNMENT,
                            p
                        )
                    } else null
                }

                fun isNotUppercase(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeBody? {
                    val paragraph = m.content[p] as org.docx4j.wml.P
                    val text = TextUtils.getText(paragraph)
                    return if (!isEmpty && (
                        text.uppercase() == text || (
                            paragraph.content.all { x ->
                                if (x is R) {
                                    val rPr = m.propertyResolver.getEffectiveRPr(x.rPr, pPr)
                                    rPr.caps != null && rPr.caps.isVal
                                } else {
                                    true
                                }
                            }
                            )
                        )
                    ) {
                        MistakeBody(TEXT_HEADER_BODY_UPPERCASE, p)
                    } else null
                }
            }
        }
    }
}
