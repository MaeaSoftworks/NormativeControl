package com.maeasoftworks.normativecontrol.rules

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.PFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.RFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.model.Document
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParser
import org.docx4j.TextUtils
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.wml.P
import org.docx4j.wml.R
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.IOException

open class RulesTestBase {
    lateinit var parser: DocumentParser

    fun base(p: Int, wrapper: PFunctionWrapper, condition: (DocumentError?) -> Boolean) {
        val paragraph = parser.mainDocumentPart.content[p] as P
        assert(
            wrapper.function(
                "",
                p,
                parser.resolver.getEffectivePPr(paragraph.pPr),
                TextUtils.getText(paragraph).isEmpty(),
                parser.mainDocumentPart
            ).let(condition)
        )
    }

    fun base(p: Int, wrapper: RFunctionWrapper, condition: (DocumentError?) -> Boolean) {
        val paragraph = parser.mainDocumentPart.content[p] as P
        assert(
            wrapper.function(
                "",
                p,
                0,
                parser.resolver.getEffectiveRPr((paragraph.content[0] as R).rPr, paragraph.pPr),
                TextUtils.getText(paragraph).isEmpty(),
                parser.mainDocumentPart
            ).let(condition)
        )
    }

    fun createParser(path: String): DocumentParser {
        (LoggerFactory.getLogger("org.docx4j") as Logger).level = Level.ERROR
        try {
            val parser = DocumentParser(
                Document(
                    "test",
                    "test",
                    FileInputStream("src/test/resources/${path}.docx").readAllBytes()
                )
            )
            parser.init()
            return parser
        } catch (e: IOException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        } catch (e: Docx4JException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        }
    }
}