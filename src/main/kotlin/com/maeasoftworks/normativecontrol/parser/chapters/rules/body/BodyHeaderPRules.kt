package com.maeasoftworks.normativecontrol.parser.chapters.rules.body

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.docx4j.TextUtils
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*

object BodyHeaderPRules {

    fun headerPJustifyCheck(
        documentId: String,
        p: Int,
        pPr: PPr,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        return if (pPr.jc != null && pPr.jc.`val` != JcEnumeration.LEFT) {
            DocumentError(
                documentId,
                p,
                0,
                if (isEmpty) ErrorType.TEXT_WHITESPACE_AFTER_HEADER_ALIGNMENT else ErrorType.TEXT_HEADER_BODY_ALIGNMENT
            )
        } else null
    }

    fun headerPUppercaseCheck(
        documentId: String,
        p: Int,
        pPr: PPr,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart
    ): DocumentError? {
        val paragraph = mainDocumentPart.content[p] as P
        val text = TextUtils.getText(paragraph)
        return if (text.uppercase() == text || (paragraph.content.all {x ->
                if (x is R) {
                    val rPr = mainDocumentPart.propertyResolver.getEffectiveRPr(x.rPr, pPr)
                    rPr.caps != null && rPr.caps.isVal
                } else {
                    true
                }
        })) {
            DocumentError(
                documentId,
                p,
                -1,
                ErrorType.TEXT_HEADER_BODY_UPPERCASE
            )
        } else null
    }
}