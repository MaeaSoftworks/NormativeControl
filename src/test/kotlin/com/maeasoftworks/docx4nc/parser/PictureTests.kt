package com.maeasoftworks.docx4nc.parser

import org.junit.jupiter.api.Test

class PictureTests : ParserTestFactory(PictureTests::class) {
    @Test
    fun `picture found properly`() {
        val parser = createParser("pictureFinder.docx")
        parser.findChapters()
    }
}