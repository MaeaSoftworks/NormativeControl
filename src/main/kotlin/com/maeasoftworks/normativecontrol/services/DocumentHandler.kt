package com.maeasoftworks.normativecontrol.services

import com.maeasoftworks.normativecontrol.components.DocumentParserBuilder
import com.maeasoftworks.normativecontrol.dtos.Document
import com.maeasoftworks.normativecontrol.dtos.FailureType
import com.maeasoftworks.normativecontrol.dtos.Result
import com.maeasoftworks.normativecontrol.dtos.State
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
            document.result = Result(FailureType.FILE_READING_ERROR)
            return
        }
        val parser = factory.build(docx, document.id)
        val errors = parser.runStyleCheck()
        document.result = Result(errors)
    }
}
