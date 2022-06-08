package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.docx4nc.parsers.DocumentParser
import com.maeasoftworks.normativecontrol.dto.Document
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(DocumentManager::class)
class DocumentParserFactory {
    fun create(document: Document): DocumentParser {
        return DocumentParser(document.data, document.password)
    }
}
