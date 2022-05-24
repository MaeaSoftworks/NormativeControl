package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.parser.model.Document
import com.maeasoftworks.normativecontrol.services.DocumentManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(DocumentManager::class)
class DocumentParserFactory {
    fun create(document: Document): DocumentParser {
        return DocumentParser(document)
    }
}