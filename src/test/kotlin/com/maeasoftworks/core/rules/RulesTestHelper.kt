package com.maeasoftworks.core.rules

import com.maeasoftworks.core.enums.MistakeType
import com.maeasoftworks.core.model.MistakeInner
import com.maeasoftworks.core.parsers.DocumentParser
import com.maeasoftworks.core.utils.PFunction
import com.maeasoftworks.core.utils.RFunction
import org.docx4j.TextUtils
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.wml.P
import org.docx4j.wml.R
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.IOException

open class RulesTestHelper {
    lateinit var parser: DocumentParser

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
            return DocumentParser(ByteArrayInputStream(FileInputStream("src/test/resources/$path.docx").readAllBytes()))
        } catch (e: IOException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        } catch (e: Docx4JException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        }
    }
}
