@file:Suppress("UNUSED_PARAMETER")

package com.maeasoftworks.docx4nc.model

import com.maeasoftworks.docx4nc.enums.MistakeType.*
import org.docx4j.TextUtils
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.JcEnumeration
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr
import kotlin.math.abs
import kotlin.math.floor

/**
 * Объект Rules имеет в себе все общие правила «по методичке»
 *
 * @author prmncr
 */
object Rules {
    object Default {
        object Common {
            /**
             * Объект параграф включает в себя функции для проверки параграфа на наличие ошибок:
             *
             * @author prmncr
             */
            object P {

                /**
                 * Функция, которая проверяет наличие «рамки» у параграфа
                 *
                 * @param p  параграф
                 * @param r  run(пробегает внутри параграфа по его содержанию)
                 * @param rPr  run Properties(здесь хранятся все свойства run)
                 * @param isEmpty  переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m  переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun notBordered(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
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

                /**
                 * Функция, которая проверяет есть ли заливка у проверяемого параграфа
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr run Properties(здесь хранятся все свойства run)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart)
                 *
                 * @author prmncr
                 */
                fun hasNotBackground(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_BACKGROUND_FILL else TEXT_COMMON_BACKGROUND_FILL, p)
                    } else null
                }
            }

            /**
             * Объект «run», этот объект «пробегает» внутри параграфа и находит в нем ошибки
             *
             * @author prmncr
             */
            object R {

                /**
                 * Функция, которая проверяет шрифт текста внутри параграфа, текст должен быть строго TimesNewRoman
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr run Properties(здесь хранятся все свойства run)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun isTimesNewRoman(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (rPr.rFonts.ascii != "Times New Roman") {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_FONT else TEXT_COMMON_FONT,
                            p,
                            r,
                            "${rPr.rFonts.ascii}/Times New Roman"
                        )
                    } else null
                }

                /**
                 * Функция проверяющая размер шрифта, размер шрифта должен быть строго 14
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr run Properties(здесь хранятся все свойства run)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun fontSizeIs14(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (rPr.sz.`val`.toInt() / 2 != 14) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_INCORRECT_FONT_SIZE else TEXT_COMMON_INCORRECT_FONT_SIZE,
                            p,
                            r,
                            "${rPr.sz.`val`.toInt() / 2}/14"
                        )
                    } else null
                }

                /**
                 * Функция, которая проверяет текст внутри параграфа на наличие курсива
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun notItalic(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (!(rPr.i == null || !rPr.i.isVal)) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_ITALIC else TEXT_COMMON_ITALIC_TEXT, p, r)
                    } else null
                }

                /**
                 * Функция, которая проверяет текст внутри параграфа на то является ли он зачеркнутым
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun notCrossedOut(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (!(rPr.strike == null || !rPr.strike.isVal)) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_STRIKETHROUGH else TEXT_COMMON_STRIKETHROUGH, p, r)
                    } else null
                }

                /**
                 * Функция, которая проверяет текст внутри параграфа на то является ли он выделенным
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun notHighlighted(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_HIGHLIGHT else TEXT_COMMON_HIGHLIGHT, p, r)
                    } else null
                }

                /**
                 * Функция, которая проверяет текст внутри параграфа на то является ли цвет шрифта черным
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun isBlack(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (rPr.color != null && rPr.color.`val` != "000000" && rPr.color.`val` != "auto") {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_TEXT_COLOR else TEXT_COMMON_TEXT_COLOR,
                            p,
                            r,
                            "${rPr.color.`val`}/black"
                        )
                    } else null
                }

                /**
                 * Функция, которая проверяет текст внутри параграфа, а конкретнее расстояние между символами
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun letterSpacingIs0(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
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

                /**
                 * Функция, которая проверяет заголовок на выравнивание по центру
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun justifyIsCenter(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_ALIGNMENT,
                            p,
                            description = "${pPr.jc?.`val`}/${JcEnumeration.CENTER}"
                        )
                    } else null
                }

                /**
                 * Функция, которая проверяет заголовок на пробелы
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun lineSpacingIsOne(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
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

                /**
                 * Функция, которая проверяет отсутствие точки в конце заголовка
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun hasNotDotInEnd(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    val text = TextUtils.getText(m.content[p] as org.docx4j.wml.P)
                    return if (text.endsWith(".")) {
                        MistakeInner(TEXT_HEADER_REDUNDANT_DOT, p)
                    } else null
                }

                /**
                 * Функция, которая проверяет есть ли пустая строка после заголовка
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun emptyLineAfterHeaderExists(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    if (m.content.size <= p + 1) {
                        return MistakeInner(CHAPTER_EMPTY, p + 1)
                    }
                    val isNotEmpty = try {
                        TextUtils.getText(m.content[p + 1] as org.docx4j.wml.P).isNotBlank()
                    } catch (e: ClassCastException) {
                        return MistakeInner(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, p)
                    }
                    return if (isNotEmpty) {
                        MistakeInner(TEXT_HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED, p)
                    } else null
                }

                /**
                 * Функция, которая проверяет отступ 1.25 в первой строке заголовка
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun firstLineIndentIs1dot25(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (pPr.numPr != null && pPr.ind != null && pPr.ind.firstLine != null &&
                        abs(floor(pPr.ind.firstLine.toDouble() / 1440.0 * 2.54) - 1.25) <= 0.01
                    ) {
                        MistakeInner(
                            TEXT_HEADER_INDENT_FIRST_LINES, p,
                            description = "${floor(pPr.ind.firstLine.toDouble() / 1440.0 * 2.54)}/1.25"
                        )
                    } else null
                }
            }

            object R {

                /**
                 * Функция, которая проверяет верхний регистр в заголовке
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun isUppercase(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    val text = TextUtils.getText((m.content[p] as org.docx4j.wml.P).content[r] as org.docx4j.wml.R)
                    return if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_UPPERCASE else TEXT_HEADER_NOT_UPPERCASE,
                            p,
                            r
                        )
                    } else null
                }


                /**
                 * Функция, которая проверяет текст в заголовке на «жирный» шрифт
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun isBold(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (rPr.b == null || !rPr.b.isVal) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_BOLD else TEXT_HEADER_NOT_BOLD, p, r)
                    } else null
                }
            }
        }

        object RegularText {
            object P {
                /**
                 * Функция, которая проверяет выравнивание параграфа
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun justifyIsBoth(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_ALIGNMENT else TEXT_REGULAR_INCORRECT_ALIGNMENT,
                            p,
                            description = "${pPr.jc?.`val`}/${JcEnumeration.BOTH}"
                        )
                    } else null
                }

                /**
                 * Функция, которая проверяет межстрочный интервал в параграфе, он должен равняться 1.5
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun lineSpacingIsOneAndHalf(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
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

                /**
                 * Функция, которая проверяет первую строку параграфа на отступ, он должен равняться 1.25
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun firstLineIndentIs1dot25(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
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

                /**
                 * Функция, которая проверяет параграф на левый отступ, который должен равняться 0
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun leftIndentIs0(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
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

                /**
                 * Функция, которая проверяет параграф на правый отступ, который должен равняться 0
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun rightIndentIs0(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
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

                /**
                 * Функция, которая проверяет текст внутри параграфа на то не является ли он «жирным»
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun isNotBold(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (rPr.b != null && !rPr.b.isVal) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_BOLD else TEXT_REGULAR_WAS_BOLD, p, r)
                    } else null
                }

                /**
                 * Функция, которая проверяет текст внутри параграфа на то не был ли он написан в верхнем регистре
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun isNotCaps(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (rPr.caps != null && !rPr.caps.isVal) {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_UPPERCASE else TEXT_REGULAR_UPPERCASE, p, r)
                    } else null
                }

                /**
                 * Функция, которая проверяет текст внутри параграфа на «подчеркнутость»
                 *
                 * @param p параграф
                 * @param r run(пробегает внутри параграфа по его содержанию)
                 * @param rPr – run Properties(здесь хранятся все свойства run)
                 * @param isEmpty – переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m – переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun isUnderline(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (rPr.u != null && rPr.u.`val`.value() != "none") {
                        MistakeInner(if (isEmpty) TEXT_WHITESPACE_UNDERLINED else TEXT_COMMON_UNDERLINED, p, r)
                    } else null
                }
            }
        }

        object PictureTitle {
            object P {

                /**
                 * Функция, которая проверяет картинку на выравнивание по центру
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun justifyIsCenter(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else PICTURE_TITLE_NOT_CENTERED,
                            p,
                            description = "${pPr.jc.`val`}/${JcEnumeration.CENTER}"
                        )
                    } else null
                }

                /**
                 * Функция, которая проверяет что в описание картинки нет точки в конце
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun hasNotDotInEnd(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    val text = TextUtils.getText(m.content[p] as org.docx4j.wml.P)
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
                /**
                 * Функция, которая проверяет выравнивание параграфа слева
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun justifyIsLeft(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
                    return if (pPr.jc != null && pPr.jc.`val` != JcEnumeration.LEFT) {
                        MistakeInner(
                            if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_BODY_ALIGNMENT,
                            p, description = "${pPr.jc.`val`}/${JcEnumeration.LEFT}"
                        )
                    } else null
                }

                /**
                 * Функция, которая проверяет верхний регистр в параграфе в основном тексте
                 *
                 * @param p параграф
                 * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
                 * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
                 * @param m переменная типа MainDocumentPart
                 *
                 * @author prmncr
                 */
                fun isNotUppercase(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
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
                        MistakeInner(TEXT_HEADER_BODY_UPPERCASE, p)
                    } else null
                }
            }
        }
    }

    object List {
        object P {

            /**
             * Функция, которая проверяет список на выравнивание по центру
             *
             * @param p параграф
             * @param pPr paragraph Properties(здесь хранятся все свойства paragraph)
             * @param isEmpty переменная типа Boolean, сигнализирующая об отсутствие текста в параграфе/run
             * @param m переменная типа MainDocumentPart
             *
             * @author prmncr
             */
            fun justifyIsCenter(p: Int, pPr: PPr, isEmpty: Boolean, m: MainDocumentPart): MistakeInner? {
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
