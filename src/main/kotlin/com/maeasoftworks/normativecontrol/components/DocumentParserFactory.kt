package com.maeasoftworks.normativecontrol.components

import com.maeasoftworks.normativecontrol.dtos.Document
import com.maeasoftworks.normativecontrol.dtos.DocumentParser
import org.springframework.stereotype.Component

@Component
class DocumentParserFactory(
    private val params: CorrectDocumentParams,
    private val keywords: HeadersKeywords
) {
    fun create(document: Document): DocumentParser {
        return DocumentParser(document, keywords)
    }
}