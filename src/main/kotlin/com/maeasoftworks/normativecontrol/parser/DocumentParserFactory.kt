package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.chapters.HeadersKeywords
import org.springframework.stereotype.Component

@Component
class DocumentParserFactory(private val keywords: HeadersKeywords) {
    fun create(document: Document): DocumentParser {
        return DocumentParser(document, keywords)
    }
}