package com.maeasoftworks.normativecontrol.parser

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

class PictureTests : ParserTestFactory(PictureTests::class) {
    @Test
    fun `picture found properly`() {
        val parser = createParser("pictureFinder.docx")
        parser.findChapters()
    }
}