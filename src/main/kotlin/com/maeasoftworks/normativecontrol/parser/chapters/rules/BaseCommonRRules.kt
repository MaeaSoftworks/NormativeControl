package com.maeasoftworks.normativecontrol.parser.chapters.rules

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType.*
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.RPr

object BaseCommonRRules {
    fun commonRFontCheck(
        documentId: String,
        rPr: RPr,
        p: Int,
        r: Int,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (rPr.rFonts.ascii != "Times New Roman") {
            DocumentError(documentId, p, r, if (isEmpty) TEXT_WHITESPACE_FONT else TEXT_COMMON_FONT)
        } else null
    }

    fun commonRFontSizeCheck(
        documentId: String,
        rPr: RPr,
        p: Int,
        r: Int,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (rPr.sz.`val`.toInt() / 2 != 14) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) TEXT_WHITESPACE_INCORRECT_FONT_SIZE else TEXT_COMMON_INCORRECT_FONT_SIZE
            )
        } else null
    }

    fun commonRItalicCheck(
        documentId: String,
        rPr: RPr,
        p: Int,
        r: Int,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (!(rPr.i == null || !rPr.i.isVal)) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) TEXT_WHITESPACE_ITALIC else TEXT_COMMON_ITALIC_TEXT
            )
        } else null
    }

    fun commonRStrikeCheck(
        documentId: String,
        rPr: RPr,
        p: Int,
        r: Int,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (!(rPr.strike == null || !rPr.strike.isVal)) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) TEXT_WHITESPACE_STRIKETHROUGH else TEXT_COMMON_STRIKETHROUGH
            )
        } else null
    }

    fun commonRHighlightCheck(
        documentId: String,
        rPr: RPr,
        p: Int,
        r: Int,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) TEXT_WHITESPACE_HIGHLIGHT else TEXT_COMMON_HIGHLIGHT
            )
        } else null
    }

    fun commonRColorCheck(
        documentId: String,
        rPr: RPr,
        p: Int,
        r: Int,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (rPr.color != null && rPr.color.`val` != "FFFFFF" && rPr.color.`val` != "auto") {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) TEXT_WHITESPACE_TEXT_COLOR else TEXT_COMMON_TEXT_COLOR
            )
        } else null
    }
}