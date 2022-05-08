package com.maeasoftworks.normativecontrol.parser

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GeneralTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "general"

    @Test
    fun `annotation validated properly`() {
        val parser = base.createParser(directory, "full test 1.docx")
        parser.findNodes()
        parser.detectNodes()
        parser.verifyNodes()
        parser.verifyAnnotation()
        //Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }
}