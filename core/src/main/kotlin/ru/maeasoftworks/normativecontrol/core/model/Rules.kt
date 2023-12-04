package ru.maeasoftworks.normativecontrol.core.model

import org.docx4j.TextUtils
import org.docx4j.wml.JcEnumeration
import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.enums.CaptureType
import ru.maeasoftworks.normativecontrol.core.enums.MistakeType.*
import ru.maeasoftworks.normativecontrol.core.utils.*
import kotlin.math.abs
import kotlin.math.floor

object Rules {
    object Default {
        object Common {
            object P {
                val notBordered: PFunction = PFunctionFactory.create(
                    { pBdr },
                    { _, _, isEmpty, bdr ->
                        if (bdr != null && (bdr.left.`val`.name != "NIL" || bdr.right.`val`.name != "NIL" || bdr.top.`val`.name != "NIL" || bdr.bottom.`val`.name != "NIL")) {
                            Mistake(if (isEmpty) TEXT_WHITESPACE_BORDER else TEXT_COMMON_BORDER, CaptureType.P)
                        } else {
                            null
                        }
                    }
                )

                val hasNotBackground = PFunctionFactory.create(
                    { shd },
                    { _, _, isEmpty, shd ->
                        if (shd != null && shd.fill != null && shd.fill != "FFFFFF") {
                            Mistake(if (isEmpty) TEXT_WHITESPACE_BACKGROUND_FILL else TEXT_COMMON_BACKGROUND_FILL, CaptureType.P)
                        } else {
                            null
                        }
                    }
                )
            }

            object R {
                val isTimesNewRoman = createRFunction(
                    { rFonts?.ascii },
                    TEXT_COMMON_FONT,
                    { _, _, _, ascii -> ascii != "Times New Roman" },
                    { it },
                    "Times New Roman"
                )

                val fontSizeIs14 = createRFunction(
                    { sz },
                    TEXT_COMMON_INCORRECT_FONT_SIZE,
                    { _, _, _, sz -> sz?.`val` != null && sz.`val`.toInt() / 2 != 14 },
                    { it?.`val`?.toInt()?.div(2)?.toString() },
                    "14"
                )

                val notItalic = createRFunction(
                    { i },
                    TEXT_COMMON_ITALIC_TEXT,
                    { _, _, _, i -> !(i == null || !i.isVal) }
                )

                val notCrossedOut = createRFunction(
                    { strike },
                    TEXT_COMMON_STRIKETHROUGH,
                    { _, _, _, strike -> !(strike == null || !strike.isVal) }
                )

                val notHighlighted = createRFunction(
                    { highlight },
                    TEXT_COMMON_HIGHLIGHT,
                    { _, _, _, highlight -> !(highlight == null || highlight.`val` == "white") }
                )

                val isBlack = createRFunction(
                    { color },
                    TEXT_COMMON_TEXT_COLOR,
                    { _, _, _, color -> color != null && color.`val` != "000000" && color.`val` != "auto" },
                    { it?.`val` },
                    "black"
                )

                val letterSpacingIs0 = createRFunction(
                    { spacing },
                    TEXT_COMMON_RUN_SPACING,
                    { _, _, _, spacing -> spacing != null && spacing.`val` != null && spacing.`val`.toDouble() != 0.0 },
                    { it?.`val`?.toDouble()?.toString() },
                    "0"
                )
            }
        }

        object Header {
            object P {
                val justifyIsCenter = PFunctionFactory.create(
                    { jc },
                    { _, _, isEmpty, jc ->
                        if (jc == null || jc.`val` != JcEnumeration.CENTER) {
                            Mistake(
                                if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_ALIGNMENT,
                                CaptureType.P,
                                jc?.`val`.toString(),
                                JcEnumeration.CENTER.toString()
                            )
                        } else {
                            null
                        }
                    }
                )

                val lineSpacingIsOne = PFunctionFactory.create(
                    { spacing },
                    { _, _, _, s ->
                        if (s != null && s.line != null && s.line.toDouble() != 240.0) {
                            Mistake(
                                TEXT_HEADER_LINE_SPACING,
                                CaptureType.P,
                                "%.2f".format(s.line.toDouble() / 240.0),
                                "1"
                            )
                        } else {
                            null
                        }
                    }
                )

                val hasNotDotInEnd: PFunction = { _, p, _ ->
                    if (TextUtils.getText(p).endsWith(".")) {
                        Mistake(TEXT_HEADER_REDUNDANT_DOT, CaptureType.P)
                    } else {
                        null
                    }
                }

                // todo move to main loop
                // val emptyLineAfterHeaderExists: PFunction = { pPos, _, _, d ->
                //    if (d.doc.content.size <= pPos + 1) Mistake(CHAPTER_EMPTY, pPos + 1)
                //    var caught: Mistake? = null
                //    val isNotEmpty = try {
                //        TextUtils.getText(d.doc.content[pPos + 1] as org.docx4j.wml.P).isNotBlank()
                //    } catch (e: ClassCastException) {
                //        caught = Mistake(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, pPos)
                //        false
                //    }
                //    caught ?: if (isNotEmpty) Mistake(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, pPos) else null
                // }

                val firstLineIndentIs1dot25 = PFunctionFactory.create(
                    { numPr },
                    { ind },
                    { _, _, _, n, i ->
                        if (n != null && i != null && i.firstLine != null && abs(floor(i.firstLine.toDouble() / 1440.0 * 2.54) - 1.25) <= 0.01) {
                            Mistake(
                                TEXT_HEADER_INDENT_FIRST_LINES,
                                CaptureType.P,
                                floor(i.firstLine.toDouble() / 1440.0 * 2.54).toString(),
                                "1.25"
                            )
                        } else {
                            null
                        }
                    }
                )

                val isAutoHyphenSuppressed = PFunctionFactory.create(
                    { suppressAutoHyphens },
                    { _, _, _, s ->
                        if ((s == null || !s.isVal) && getContext()!!.mlPackage.mainDocumentPart.documentSettingsPart.jaxbElement.autoHyphenation?.isVal == true) {
                            Mistake(TEXT_HEADER_AUTO_HYPHEN, CaptureType.P)
                        } else {
                            null
                        }
                    }
                )
            }

            object R {
                val isUppercase = createRFunction(
                    { caps },
                    TEXT_HEADER_NOT_UPPERCASE,
                    { r, _, _, caps ->
                        val text = TextUtils.getText(r)
                        !(text.uppercase() == text || (caps != null && caps.isVal))
                    }
                )

                val isBold = createRFunction(
                    { b },
                    TEXT_HEADER_NOT_BOLD,
                    { _, _, _, b -> b == null || !b.isVal }
                )
            }
        }

        object RegularText {
            object P {
                val justifyIsBoth: PFunction = { _, p, isEmpty ->
                    val jc = p.getPropertyValue { jc }
                    if (jc == null || jc.`val` != JcEnumeration.BOTH) {
                        Mistake(
                            if (isEmpty) TEXT_WHITESPACE_ALIGNMENT else TEXT_REGULAR_INCORRECT_ALIGNMENT,
                            CaptureType.P,
                            jc?.`val`?.toString(),
                            JcEnumeration.BOTH.toString()
                        )
                    } else {
                        null
                    }
                }

                val lineSpacingIsOneAndHalf: PFunction = { _, p, isEmpty ->
                    val s = p.getPropertyValue { spacing }
                    if (s != null && s.line != null) {
                        if (s.lineRule.value() == "auto" && s.line.toDouble() != 360.0) {
                            Mistake(
                                if (isEmpty) TEXT_WHITESPACE_LINE_SPACING else TEXT_REGULAR_LINE_SPACING,
                                CaptureType.P,
                                "${s.line.toDouble() / 240.0}",
                                "1.5"
                            )
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }

                val firstLineIndentIs1dot25 = PFunctionFactory.create(
                    { numPr },
                    { ind },
                    { _, _, isEmpty, n, i ->
                        if (n != null && i != null && i.firstLine != null && abs(floor(i.firstLine.toDouble() / 1440.0 * 2.54) - 1.25) <= 0.01) {
                            Mistake(
                                if (isEmpty) TEXT_WHITESPACE_INDENT_FIRST_LINES else TEXT_REGULAR_INDENT_FIRST_LINES,
                                CaptureType.P,
                                "${floor(i.firstLine.toDouble() / 1440 * 2.54)}",
                                "1.25"
                            )
                        } else {
                            null
                        }
                    }
                )

                val leftIndentIs0 = PFunctionFactory.create(
                    { numPr },
                    { ind },
                    { _, _, _, n, i ->
                        if (n != null && i != null && i.left != null && i.left.toDouble() != 0.0) {
                            Mistake(
                                TEXT_COMMON_INDENT_LEFT,
                                CaptureType.P,
                                "${i.left.toDouble() / 240.0}",
                                "0"
                            )
                        } else {
                            null
                        }
                    }
                )

                val rightIndentIs0 = PFunctionFactory.create(
                    { ind },
                    { numPr },
                    { _, _, isEmpty, i, n ->
                        if (n != null && i != null && i.right != null && i.right.toDouble() != 0.0) {
                            Mistake(
                                if (isEmpty) TEXT_WHITESPACE_INDENT_RIGHT else TEXT_COMMON_INDENT_RIGHT,
                                CaptureType.P,
                                "${i.right.toDouble() / 240.0}",
                                "0"
                            )
                        } else {
                            null
                        }
                    }
                )
            }

            object R {
                val isNotBold = createRFunction(
                    { b },
                    TEXT_REGULAR_WAS_BOLD,
                    { _, _, _, b -> b != null && !b.isVal }
                )

                val isNotCaps = createRFunction(
                    { caps },
                    TEXT_REGULAR_UPPERCASE,
                    { _, _, _, caps -> caps != null && !caps.isVal }
                )

                val isUnderline = createRFunction(
                    { b },
                    TEXT_COMMON_UNDERLINED,
                    { _, _, _, u -> u != null && u.isVal }
                )
            }
        }

        object PictureTitle {
            object P {
                val justifyIsCenter = PFunctionFactory.create(
                    { jc },
                    { _, _, isEmpty, jc ->
                        if (jc == null || jc.`val` != JcEnumeration.CENTER) {
                            Mistake(
                                if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else PICTURE_TITLE_NOT_CENTERED,
                                CaptureType.P,
                                jc?.`val`?.toString(),
                                JcEnumeration.CENTER.toString()
                            )
                        } else {
                            null
                        }
                    }
                )

                val hasNotDotInEnd: PFunction = { _, p, _ ->
                    if (TextUtils.getText(p).endsWith(".")) {
                        Mistake(PICTURE_TITLE_ENDS_WITH_DOT, CaptureType.P)
                    } else {
                        null
                    }
                }
            }
        }
    }

    object Body {
        object Header {
            object P {
                val justifyIsLeft = PFunctionFactory.create(
                    { jc },
                    { _, _, isEmpty, jc ->
                        if (jc != null && jc.`val` != JcEnumeration.LEFT) {
                            Mistake(
                                if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_BODY_ALIGNMENT,
                                CaptureType.P,
                                jc.`val`.toString(),
                                JcEnumeration.LEFT.toString()
                            )
                        } else {
                            null
                        }
                    }
                )

                val isNotUppercase: PFunction = { _, p, isEmpty ->
                    val text = TextUtils.getText(p)
                    if (!isEmpty && (text.uppercase() == text || p.content.all { if (it is R) it.getPropertyValue { caps }.let { caps -> caps != null && caps.isVal } else false })
                    ) {
                        Mistake(TEXT_HEADER_BODY_UPPERCASE, CaptureType.P)
                    } else {
                        null
                    }
                }
            }
        }
    }
}