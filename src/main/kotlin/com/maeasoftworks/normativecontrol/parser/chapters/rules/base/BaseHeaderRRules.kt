package com.maeasoftworks.normativecontrol.parser.chapters.rules.base

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType.*
import org.docx4j.TextUtils
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.P
import org.docx4j.wml.R
import org.docx4j.wml.RPr

object BaseHeaderRRules {
    fun headerRUppercaseCheck(
        documentId: String,
        p: Int,
        r: Int,
        rPr: RPr,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        val text = TextUtils.getText((mainDocumentPart.content[p] as P).content[r] as R)
        return if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_UPPERCASE else TEXT_HEADER_NOT_UPPERCASE
            )
        } else null
    }

    fun headerRBoldCheck(
        documentId: String,
        p: Int,
        r: Int,
        rPr: RPr,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        val text = TextUtils.getText(p)
        return if (rPr.b == null || !rPr.b.isVal) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) TEXT_WHITESPACE_AFTER_HEADER_BOLD else TEXT_HEADER_NOT_BOLD
            )
        } else null
    }
}