package com.maeasoftworks.normativecontrol.parser

import org.docx4j.openpackaging.exceptions.Docx4JException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.IOException

@Component
class TestBase {
    @Autowired
    protected lateinit var factory: DocumentParserFactory

    fun createParser(directory: String, filename: String, useFullPath: Boolean = false): DocumentParser {
        try {
            val parser = factory.create(
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