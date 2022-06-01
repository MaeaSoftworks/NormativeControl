package com.maeasoftworks.normativecontrol.parser

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.maeasoftworks.normativecontrol.entities.Mistake
import com.maeasoftworks.normativecontrol.parser.enums.MistakeType
import com.maeasoftworks.normativecontrol.parser.model.Document
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParser
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.slf4j.LoggerFactory
import java.io.FileInputStream
import java.io.IOException
import kotlin.reflect.KClass

open class ParserTestFactory(testClass: KClass<*>) {
    private val directory: String

    init {
        (LoggerFactory.getLogger("org.docx4j") as Logger).level = Level.ERROR
        directory = testClass.simpleName!!.removeSuffix("Tests").lowercase()
    }

    protected fun errorAssert(found: MutableList<Mistake>, vararg expected: MistakeType) {
        assert(found.size == expected.size) { "Expected: ${expected.size} errors\nFound: ${found.size}" }
        for (i in 0 until found.size) {
            assert(found[i].mistakeType == expected[i]) { "Expected: ${expected[i].name}\nFound: ${found[i].mistakeType.name}" }
        }
    }

    protected fun createParser(filename: String, useFullPath: Boolean = false): DocumentParser {
        try {
            val parser = DocumentParser(
                Document(
                    "test",
                    "test",
                    FileInputStream(if (useFullPath) "src/test/resources/$filename" else "src/test/resources/$directory/$filename").readAllBytes()
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