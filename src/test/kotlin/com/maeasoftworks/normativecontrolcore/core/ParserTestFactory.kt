package com.maeasoftworks.normativecontrolcore.core

import com.maeasoftworks.normativecontrolcore.core.parsers.DocumentParser
import org.docx4j.openpackaging.exceptions.Docx4JException
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.IOException
import kotlin.reflect.KClass

open class ParserTestFactory(testClass: KClass<*>) {
    private val directory: String

    init {
        directory = testClass.simpleName!!.removeSuffix("Tests").lowercase()
    }

    protected fun createParser(filename: String, useFullPath: Boolean = false): DocumentParser {
        return try {
            DocumentParser(
                ByteArrayInputStream(
                    FileInputStream(
                        if (useFullPath) {
                            "src/test/resources/$filename"
                        } else {
                            "src/test/resources/$directory/$filename"
                        }
                    ).readAllBytes()
                )
            )
        } catch (e: IOException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        } catch (e: Docx4JException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        }
    }
}
