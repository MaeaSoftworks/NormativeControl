package com.prmncr.normativecontrol.parser

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PictureTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "pictures"

    @Test
    fun `picture found properly`() {
        val parser = base.createParser(directory, "pictureFinder.docx")
        parser.findSectors()
    }
}