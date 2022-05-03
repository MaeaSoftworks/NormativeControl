package com.maeasoftworks.normativecontrol.components

import com.maeasoftworks.normativecontrol.dtos.DocumentParser
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.springframework.stereotype.Component

@Component
class DocumentParserBuilder(
    private val params: CorrectDocumentParams,
    private val keywords: HeadersKeywords
) {
    fun build(docx: WordprocessingMLPackage, documentId: String?): DocumentParser {
        return DocumentParser(docx, params, keywords, documentId)
    }
}