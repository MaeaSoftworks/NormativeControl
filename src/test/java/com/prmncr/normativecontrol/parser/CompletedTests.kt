package com.prmncr.normativecontrol.parser

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
class CompletedTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "full examples"

    @Test
    fun annotationIsCorrect() {
        val parser = base.createParser(directory, "full test 1.docx")
        parser.findSectors()
        parser.detectNodes()
        parser.findIncorrectNodes()
        parser.checkAnnotation()
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }
}