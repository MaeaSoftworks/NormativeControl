package com.prmncr.normativecontrol.components

import com.prmncr.docx4nc.DocumentParser
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.springframework.stereotype.Component

@Component
class DocumentParserBuilder(private val params: CorrectDocumentParams, private val keywords: SectorKeywords) {

    fun build(docx: WordprocessingMLPackage?): DocumentParser {
        if (docx != null) {
            return DocumentParser(docx, params, keywords)
        } else throw IllegalArgumentException()
    }
}