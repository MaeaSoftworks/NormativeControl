package com.maeasoftworks.normativecontrol.dtos.chapters

import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType
import org.docx4j.wml.RPr

object BaseRRules {
    fun commonRFontCheck(documentId: String, rPr: RPr, p: Int, r: Int, isEmpty: Boolean): DocumentError? {
        return if (rPr.rFonts.ascii != "Times New Roman") {
            DocumentError(documentId, p, r, if (isEmpty) ErrorType.TEXT_WHITESPACE_FONT else ErrorType.TEXT_COMMON_FONT)
        } else null
    }

    fun commonRColorCheck(documentId: String, rPr: RPr, p: Int, r: Int, isEmpty: Boolean): DocumentError? {
        return if (rPr.sz.`val`.toInt() / 2 != 14) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_INCORRECT_FONT_SIZE else ErrorType.TEXT_COMMON_INCORRECT_FONT_SIZE
            )
        } else null
    }

    fun commonRItalicCheck(documentId: String, rPr: RPr, p: Int, r: Int, isEmpty: Boolean): DocumentError? {
        return if (!(rPr.i == null || !rPr.i.isVal)) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_ITALIC else ErrorType.TEXT_COMMON_ITALIC_TEXT
            )
        } else null
    }

    fun commonRStrikeCheck(documentId: String, rPr: RPr, p: Int, r: Int, isEmpty: Boolean): DocumentError? {
        return if (!(rPr.strike == null || !rPr.strike.isVal)) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_STRIKETHROUGH else ErrorType.TEXT_COMMON_STRIKETHROUGH
            )
        } else null
    }

    fun commonRHighlightCheck(documentId: String, rPr: RPr, p: Int, r: Int, isEmpty: Boolean): DocumentError? {
        return if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_HIGHLIGHT else ErrorType.TEXT_COMMON_HIGHLIGHT
            )
        } else null
    }
}