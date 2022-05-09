package com.maeasoftworks.normativecontrol.parser.chapters.rules

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.JcEnumeration
import org.docx4j.wml.PPr
import kotlin.math.abs
import kotlin.math.floor

object BaseRegularPRules {
    fun regularPJustifyCheck(
        documentId: String,
        p: Int,
        isEmpty: Boolean,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
            DocumentError(
                documentId,
                p,
                -1,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_ALIGNMENT else ErrorType.TEXT_REGULAR_INCORRECT_ALIGNMENT
            )
        } else null
    }

    fun regularPLineSpacingCheck(
        documentId: String,
        p: Int,
        isEmpty: Boolean,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (pPr.spacing != null && pPr.spacing.line != null) {
            if (pPr.spacing.lineRule.value() == "auto" && pPr.spacing.line.intValueExact() != 360) {
                DocumentError(
                    documentId,
                    p,
                    -1,
                    if (isEmpty) ErrorType.TEXT_WHITESPACE_LINE_SPACING else ErrorType.TEXT_REGULAR_LINE_SPACING
                )
            } else null
        } else null
    }

    fun regularPFirstLineIndentCheck(
        documentId: String,
        p: Int,
        isEmpty: Boolean,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (pPr.ind != null && abs(floor(pPr.ind.firstLine.intValueExact() / 1440 * 2.54) - 1.25) <= 0.01) {
            DocumentError(
                documentId,
                p,
                -1,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_INDENT_FIRST_LINES else ErrorType.TEXT_COMMON_INDENT_FIRST_LINES
            )
        } else null
    }

    fun regularPLeftIndentCheck(
        documentId: String,
        p: Int,
        isEmpty: Boolean,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (pPr.ind != null && pPr.ind.left != null) {
            DocumentError(
                documentId,
                p,
                -1,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_INDENT_LEFT else ErrorType.TEXT_COMMON_INDENT_LEFT
            )
        } else null
    }

    fun regularPRightIndentCheck(
        documentId: String,
        p: Int,
        isEmpty: Boolean,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (pPr.ind != null && pPr.ind.right != null) {
            DocumentError(
                documentId,
                p,
                -1,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_INDENT_RIGHT else ErrorType.TEXT_COMMON_INDENT_RIGHT
            )
        } else null
    }
}