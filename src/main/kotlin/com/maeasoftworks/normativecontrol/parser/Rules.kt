@file:Suppress("UNUSED_PARAMETER")

package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.entities.Mistake
import com.maeasoftworks.normativecontrol.parser.enums.MistakeType
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
                fun notBordered(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.pBdr != null) {
                        Mistake(
                            documentId,
                            p,
                            -1,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_BORDER else MistakeType.TEXT_COMMON_BORDER
                        )
                    } else null
                }

                fun hasNotBackground(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
                        Mistake(
                            documentId,
                            p,
                            -1,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_BACKGROUND_FILL else MistakeType.TEXT_COMMON_BACKGROUND_FILL
                        )
                    } else null
                }
            }

            object R {
                fun isTimesNewRoman(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (rPr.rFonts.ascii != "Times New Roman") {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_FONT else MistakeType.TEXT_COMMON_FONT
                        )
                    } else null
                }

                fun fontSizeIs14(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (rPr.sz.`val`.toInt() / 2 != 14) {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_INCORRECT_FONT_SIZE else MistakeType.TEXT_COMMON_INCORRECT_FONT_SIZE
                        )
                    } else null
                }

                fun notItalic(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (!(rPr.i == null || !rPr.i.isVal)) {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_ITALIC else MistakeType.TEXT_COMMON_ITALIC_TEXT
                        )
                    } else null
                }

                fun notCrossedOut(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (!(rPr.strike == null || !rPr.strike.isVal)) {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_STRIKETHROUGH else MistakeType.TEXT_COMMON_STRIKETHROUGH
                        )
                    } else null
                }

                fun notHighlighted(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_HIGHLIGHT else MistakeType.TEXT_COMMON_HIGHLIGHT
                        )
                    } else null
                }

                fun isBlack(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (rPr.color != null && rPr.color.`val` != "000000" && rPr.color.`val` != "auto") {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_TEXT_COLOR else MistakeType.TEXT_COMMON_TEXT_COLOR
                        )
                    } else null
                }

                fun letterSpacingIs0(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (rPr.spacing != null && rPr.spacing.`val` != null && rPr.spacing.`val`.intValueExact() != 0) {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_RUN_SPACING else MistakeType.TEXT_COMMON_RUN_SPACING
                        )
                    } else null
                }
            }
        }

        object Header {
            object P {
                fun justifyIsCenter(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        Mistake(
                            documentId,
                            p,
                            0,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else MistakeType.TEXT_HEADER_ALIGNMENT
                        )
                    } else null
                }

                fun lineSpacingIsOne(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.spacing != null && pPr.spacing.line != null && pPr.spacing.line.intValueExact() != 240) {
                        Mistake(documentId, p, 0, MistakeType.TEXT_HEADER_LINE_SPACING)
                    } else null
                }

                fun hasNotDotInEnd(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    val text = TextUtils.getText(mainDocumentPart.content[p] as org.docx4j.wml.P)
                    return if (text.endsWith(".")) {
                        Mistake(documentId, p, -1, MistakeType.TEXT_HEADER_REDUNDANT_DOT)
                    } else null
                }

                fun emptyLineAfterHeaderExists(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    if (mainDocumentPart.content.size <= p + 1) {
                        return Mistake(
                            documentId,
                            p + 1,
                            MistakeType.CHAPTER_EMPTY
                        )
                    }
                    val isNotEmpty = try {
                        TextUtils.getText(mainDocumentPart.content[p + 1] as org.docx4j.wml.P).isNotEmpty()
                    } catch (e: ClassCastException) {
                        return Mistake(
                            documentId,
                            p + 1,
                            MistakeType.TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED
                        )
                    }
                    return if (isNotEmpty) {
                        Mistake(
                            documentId,
                            p + 1,
                            MistakeType.TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED
                        )
                    } else null
                }
            }

            object R {
                fun isUppercase(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    val text =
                        TextUtils.getText((mainDocumentPart.content[p] as org.docx4j.wml.P).content[r] as org.docx4j.wml.R)
                    return if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_AFTER_HEADER_UPPERCASE else MistakeType.TEXT_HEADER_NOT_UPPERCASE
                        )
                    } else null
                }

                fun isBold(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (rPr.b == null || !rPr.b.isVal) {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_AFTER_HEADER_BOLD else MistakeType.TEXT_HEADER_NOT_BOLD
                        )
                    } else null
                }
            }
        }

        object RegularText {
            object P {
                fun justifyIsBoth(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
                        Mistake(
                            documentId,
                            p,
                            -1,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_ALIGNMENT else MistakeType.TEXT_REGULAR_INCORRECT_ALIGNMENT
                        )
                    } else null
                }

                fun lineSpacingIsOneAndHalf(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.spacing != null && pPr.spacing.line != null) {
                        if (pPr.spacing.lineRule.value() == "auto" && pPr.spacing.line.intValueExact() != 360) {
                            Mistake(
                                documentId,
                                p,
                                -1,
                                if (isEmpty) MistakeType.TEXT_WHITESPACE_LINE_SPACING else MistakeType.TEXT_REGULAR_LINE_SPACING
                            )
                        } else null
                    } else null
                }

                fun firstLineIndentIs1dot25(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.numPr != null && pPr.ind != null && pPr.ind.firstLine != null && abs(floor(pPr.ind.firstLine.intValueExact() / 1440 * 2.54) - 1.25) <= 0.01) {
                        Mistake(
                            documentId,
                            p,
                            -1,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_INDENT_FIRST_LINES else MistakeType.TEXT_COMMON_INDENT_FIRST_LINES
                        )
                    } else null
                }

                fun leftIndentIs0(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.numPr != null && pPr.ind != null && pPr.ind.left != null && pPr.ind.left.intValueExact() != 0) {
                        Mistake(
                            documentId,
                            p,
                            -1,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_INDENT_LEFT else MistakeType.TEXT_COMMON_INDENT_LEFT
                        )
                    } else null
                }

                fun rightIndentIs0(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.numPr != null && pPr.ind != null && pPr.ind.right != null && pPr.ind.right.intValueExact() != 0) {
                        Mistake(
                            documentId,
                            p,
                            -1,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_INDENT_RIGHT else MistakeType.TEXT_COMMON_INDENT_RIGHT
                        )
                    } else null
                }
            }

            object R {
                fun isNotBold(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (rPr.b != null && !rPr.b.isVal) {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_BOLD else MistakeType.TEXT_REGULAR_WAS_BOLD
                        )
                    } else null
                }

                fun isNotCaps(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (rPr.caps != null && !rPr.caps.isVal) {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_UPPERCASE else MistakeType.TEXT_REGULAR_UPPERCASE
                        )
                    } else null
                }

                fun isUnderline(
                    documentId: String,
                    p: Int,
                    r: Int,
                    rPr: RPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (rPr.u != null && rPr.u.`val`.value() != "none") {
                        Mistake(
                            documentId,
                            p,
                            r,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_UNDERLINED else MistakeType.TEXT_COMMON_UNDERLINED
                        )
                    } else null
                }
            }
        }

        object PictureTitle {
            object P {
                fun justifyIsCenter(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        Mistake(
                            documentId,
                            p,
                            0,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else MistakeType.PICTURE_TITLE_NOT_CENTERED
                        )
                    } else null
                }

                fun hasNotDotInEnd(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    val text = TextUtils.getText(mainDocumentPart.content[p] as org.docx4j.wml.P)
                    return if (text.endsWith(".")) {
                        Mistake(documentId, p, -1, MistakeType.PICTURE_TITLE_ENDS_WITH_DOT)
                    } else null
                }
            }
        }
    }

    object Body {
        object Header {
            object P {
                fun justifyIsLeft(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    return if (pPr.jc != null && pPr.jc.`val` != JcEnumeration.LEFT) {
                        Mistake(
                            documentId,
                            p,
                            0,
                            if (isEmpty) MistakeType.TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else MistakeType.TEXT_HEADER_BODY_ALIGNMENT
                        )
                    } else null
                }

                fun isNotUppercase(
                    documentId: String,
                    p: Int,
                    pPr: PPr,
                    isEmpty: Boolean,
                    mainDocumentPart: MainDocumentPart
                ): Mistake? {
                    val paragraph = mainDocumentPart.content[p] as org.docx4j.wml.P
                    val text = TextUtils.getText(paragraph)
                    return if (!isEmpty && (text.uppercase() == text || (paragraph.content.all { x ->
                            if (x is R) {
                                val rPr = mainDocumentPart.propertyResolver.getEffectiveRPr(x.rPr, pPr)
                                rPr.caps != null && rPr.caps.isVal
                            } else {
                                true
                            }
                        }))) {
                        Mistake(
                            documentId,
                            p,
                            -1,
                            MistakeType.TEXT_HEADER_BODY_UPPERCASE
                        )
                    } else null
                }
            }
        }
    }
}