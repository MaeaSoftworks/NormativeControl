package com.maeasoftworks.normativecontrol.parser.chapters.rules.base

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.RPr

object BaseRegularRRules {

    fun regularRBoldCheck(
        documentId: String,
        p: Int,
        r: Int,
        rPr: RPr,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (rPr.b != null && !rPr.b.isVal) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_BOLD else ErrorType.TEXT_REGULAR_WAS_BOLD
            )
        } else null
    }

    fun regularRCapsCheck(
        documentId: String,
        p: Int,
        r: Int,
        rPr: RPr,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (rPr.caps != null && !rPr.caps.isVal) {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_UPPERCASE else ErrorType.TEXT_REGULAR_UPPERCASE
            )
        } else null
    }

    fun regularRUnderlineCheck(
        documentId: String,
        p: Int,
        r: Int,
        rPr: RPr,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (rPr.u != null && rPr.u.`val`.value() != "none") {
            DocumentError(
                documentId,
                p,
                r,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_UNDERLINED else ErrorType.TEXT_COMMON_UNDERLINED
            )
        } else null
    }
}