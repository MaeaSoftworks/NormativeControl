package com.prmncr.normativecontrol

import com.prmncr.docx4nc.DocumentParser
import com.prmncr.normativecontrol.components.DocumentParserBuilder
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.util.Assert
import java.io.FileInputStream
import java.io.IOException

@TestConfiguration
class TestsBase {
    @Autowired
    protected lateinit var factory: DocumentParserBuilder

    protected fun createParser(filename: String): DocumentParser {
        return try {
            factory.build(WordprocessingMLPackage.load(FileInputStream("src/main/resources/test files/$filename")))
        } catch (e: IOException) {
            println(e.message)
            Assert.isTrue(false, "Parser cannot be initialized!")
            factory.build(null)
        } catch (e: Docx4JException) {
            println(e.message)
            Assert.isTrue(false, "Parser cannot be initialized!")
            factory.build(null)
        }
    }
}