package com.maeasoftworks.docx4nc.rules

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.maeasoftworks.docx4nc.enums.MistakeType
import com.maeasoftworks.docx4nc.model.DocumentData
import com.maeasoftworks.docx4nc.model.MistakeInner
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import com.maeasoftworks.docx4nc.utils.PFunction
import com.maeasoftworks.docx4nc.utils.RFunction
import org.docx4j.TextUtils
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.wml.P
import org.docx4j.wml.R
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.IOException

open class RulesTestHelper {
    lateinit var parser: DocumentParser

    init {
        (LoggerFactory.getLogger("org.docx4j") as Logger).level = Level.ERROR
    }

    private fun test(p: Int, wrapper: PFunction, condition: (MistakeInner?) -> Boolean) {
        val paragraph = parser.doc.content[p] as P
        assert(
            wrapper(
                p,
                parser.propertiesStorage[paragraph],
                TextUtils.getText(paragraph).isEmpty(),
                parser
            ).let(condition)
        )
    }

    private fun test(p: Int, wrapper: RFunction, condition: (MistakeInner?) -> Boolean) {
        val paragraph = parser.doc.content[p] as P
        assert(
            wrapper(
                p,
                0,
                parser.propertiesStorage[paragraph.content[0] as R],
                TextUtils.getText(paragraph).isEmpty(),
                parser
            ).let(condition)
        )
    }

    fun testRule(p: Int, wrapper: PFunction) {
        test(p, wrapper) { it == null }
    }

    fun testRule(p: Int, wrapper: RFunction) {
        test(p, wrapper) { it == null }
    }

    fun testRule(p: Int, wrapper: PFunction, mistakeType: MistakeType) {
        test(p, wrapper) { it != null && it.mistakeType == mistakeType }
    }

    fun testRule(p: Int, wrapper: RFunction, mistakeType: MistakeType) {
        test(p, wrapper) { it != null && it.mistakeType == mistakeType }
    }

    fun createParser(path: String): DocumentParser {
        try {
            val parser =
                DocumentParser(DocumentData(FileInputStream("src/test/resources/$path.docx").readAllBytes()), "test")
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
