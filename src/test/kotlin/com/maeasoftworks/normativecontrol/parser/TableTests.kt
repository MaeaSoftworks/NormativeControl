package com.maeasoftworks.normativecontrol.parser

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TableTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "table"

    @Test
    fun `table found properly`() {
        val parser = base.createParser(directory, "tableFinder.docx")
        parser.findNodes()
    }
}