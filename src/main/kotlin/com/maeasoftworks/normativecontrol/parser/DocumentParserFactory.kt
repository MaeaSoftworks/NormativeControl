package com.maeasoftworks.normativecontrol.parser

import org.springframework.stereotype.Component

@Component
class DocumentParserFactory {
    fun create(document: Document): DocumentParser {
        return DocumentParser(document)
    }
}