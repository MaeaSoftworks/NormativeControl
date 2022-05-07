package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.components.DocumentParserFactory
import com.maeasoftworks.normativecontrol.dtos.Document
import com.maeasoftworks.normativecontrol.dtos.DocumentParser
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.IOException

@Component
class TestBase {
    @Autowired
    protected lateinit var factory: DocumentParserFactory

    fun createParser(directory: String, filename: String): DocumentParser {
        return try {
            factory.create(Document("test", "test", FileInputStream("src/test/resources/$directory/$filename").readAllBytes()))
        } catch (e: IOException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        } catch (e: Docx4JException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        }
    }
}