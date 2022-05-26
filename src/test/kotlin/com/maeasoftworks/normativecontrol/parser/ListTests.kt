package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.model.Chapter
import com.maeasoftworks.normativecontrol.parser.parsers.ChapterParser
import com.maeasoftworks.normativecontrol.parser.parsers.SimpleParser
import org.junit.jupiter.api.Test
import org.springframework.util.Assert

class ListTests : ParserTestFactory(ListTests::class) {

    @Test
    fun `list borders found properly`() {
        val parser = createParser("list size.docx")
        val mock: ChapterParser = SimpleParser(Chapter(0), parser)
        mock.validateList(0)
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }
}