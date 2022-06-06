package com.maeasoftworks.docx4nc.parser

import com.maeasoftworks.docx4nc.model.Chapter
import com.maeasoftworks.docx4nc.parsers.ChapterParser
import com.maeasoftworks.docx4nc.parsers.SimpleParser
import org.junit.jupiter.api.Test
import org.springframework.util.Assert

class ListTests : ParserTestFactory(ListTests::class) {

    @Test
    fun `list borders found properly`() {
        val parser = createParser("list size.docx")
        val mock: ChapterParser = SimpleParser(Chapter(0), parser)
        mock.validateListElement(0)
        Assert.isTrue(parser.mistakes.size == 0, "There shouldn't be any error!")
    }
}