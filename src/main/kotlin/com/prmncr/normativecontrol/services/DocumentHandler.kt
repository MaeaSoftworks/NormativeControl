package com.prmncr.normativecontrol.services

import com.prmncr.normativecontrol.components.DocumentParserBuilder
import com.prmncr.normativecontrol.dtos.Document
import com.prmncr.normativecontrol.dtos.FailureType
import com.prmncr.normativecontrol.dtos.Result
import com.prmncr.normativecontrol.dtos.State
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream

@Service
class DocumentHandler(private val factory: DocumentParserBuilder) {
    fun handle(document: Document) {
        val docx: WordprocessingMLPackage = try {
            WordprocessingMLPackage.load(ByteArrayInputStream(document.file))
        } catch (e: Docx4JException) {
            document.state = State.ERROR
            document.result =Result(FailureType.FILE_READING_ERROR)
            return
        }
        val parser = factory.build(docx)
        val errors = parser.runStyleCheck()
        document.result = Result(errors)
    }
}