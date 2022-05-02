package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.components.DocumentParserBuilder
import com.maeasoftworks.normativecontrol.dtos.DocumentParser
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.IOException

@Component
class TestBase {
    @Autowired
    protected lateinit var factory: DocumentParserBuilder

    fun createParser(directory: String, filename: String): DocumentParser {
        return try {
            factory.build(WordprocessingMLPackage.load(FileInputStream("src/test/resources/$directory/$filename")))
        } catch (e: IOException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        } catch (e: Docx4JException) {
            println(e.message)
            throw RuntimeException("Parser cannot be initialized!")
        }
    }
}