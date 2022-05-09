package com.maeasoftworks.normativecontrol.parser.chapters.rules

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType.*
import org.docx4j.TextUtils
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.JcEnumeration
import org.docx4j.wml.P
import org.docx4j.wml.PPr

object BaseHeaderPRules {
    fun headerPJustifyCheck(
        documentId: String,
        p: Int,
        isEmpty: Boolean,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
            DocumentError(
                documentId,
                p,
                0,
                if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else TEXT_HEADER_ALIGNMENT
            )
        } else null
    }

    fun headerPLineSpacingCheck(
        documentId: String,
        p: Int,
        isEmpty: Boolean,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (pPr.spacing != null && pPr.spacing.line != null && pPr.spacing.line.intValueExact() != 240) {
            DocumentError(documentId, p, 0, TEXT_HEADER_LINE_SPACING)
        } else null
    }

    fun headerPNotEndsWithDotCheck(
        documentId: String,
        p: Int,
        isEmpty: Boolean,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        val text = TextUtils.getText(mainDocumentPart.content[p] as P)
        return if (text.endsWith(".")) {
            DocumentError(documentId, p, -1, TEXT_HEADER_REDUNDANT_DOT)
        } else null
    }

    fun headerEmptyLineAfterHeaderExist(
        documentId: String,
        p: Int,
        isEmpty: Boolean,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (mainDocumentPart.content.size <= p + 1) {
            DocumentError(
                documentId,
                p + 1,
                CHAPTER_EMPTY
            )
        } else if (TextUtils.getText(mainDocumentPart.content[p + 1] as P).isNotEmpty()) {
            DocumentError(
                documentId,
                p + 1,
                HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED
            )
        } else null
    }
}