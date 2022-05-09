package com.maeasoftworks.normativecontrol.dtos.chapters

import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType.*
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

object BasePRules {
    fun commonPTextAlignCheck(documentId: String, p: Int, isEmpty: Boolean, pPr: PPr): DocumentError? {
        return if (pPr.textAlignment != null && pPr.textAlignment.`val` != "left") {
            DocumentError(
                documentId,
                p,
                -1,
                if (isEmpty) TEXT_WHITESPACE_INCORRECT_DIRECTION else TEXT_COMMON_INCORRECT_DIRECTION
            )
        } else null
    }

    fun commonPBorderCheck(documentId: String, p: Int, isEmpty: Boolean, pPr: PPr): DocumentError? {
        return if (pPr.pBdr != null) {
            DocumentError(documentId, p, -1, if (isEmpty) TEXT_WHITESPACE_BORDER else TEXT_COMMON_BORDER)
        } else null
    }

    fun commonPBackgroundCheck(documentId: String, p: Int, isEmpty: Boolean, pPr: PPr): DocumentError? {
        return if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
            DocumentError(
                documentId,
                p,
                -1,
                if (isEmpty) TEXT_WHITESPACE_BACKGROUND_FILL else TEXT_COMMON_BACKGROUND_FILL
            )
        } else null
    }
}