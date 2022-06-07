package com.maeasoftworks.docx4nc.rules

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.maeasoftworks.docx4nc.PFunction
import com.maeasoftworks.docx4nc.RFunction
import com.maeasoftworks.docx4nc.model.DocumentData
import com.maeasoftworks.docx4nc.model.MistakeBody
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.TextUtils
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.wml.P
import org.docx4j.wml.R
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.IOException

open class RulesTestBase {
    lateinit var parser: DocumentParser

    fun base(p: Int, wrapper: PFunction, condition: (MistakeBody?) -> Boolean) {
        val paragraph = parser.mainDocumentPart.content[p] as P
        assert(
            wrapper(
                p,
                parser.resolver.getEffectivePPr(paragraph.pPr),
                TextUtils.getText(paragraph).isEmpty(),
                parser.mainDocumentPart
            ).let(condition)
        )
    }

    fun base(p: Int, wrapper: RFunction, condition: (MistakeBody?) -> Boolean) {
        val paragraph = parser.mainDocumentPart.content[p] as P
        assert(
            wrapper(
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
            val parser = DocumentParser(DocumentData(FileInputStream("src/test/resources/$path.docx").readAllBytes()))
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
