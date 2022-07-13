package com.maeasoftworks.docx4nc

import com.maeasoftworks.docx4nc.enums.MistakeType.*
import com.maeasoftworks.docx4nc.model.Chapter
import com.maeasoftworks.docx4nc.parsers.ChapterParser
import com.maeasoftworks.docx4nc.parsers.SimpleParser
import org.junit.jupiter.api.Test

class ListTests : ParserTestFactory(ListTests::class) {

    @Test
    fun `list disorder found properly`() {
        val parser = createParser("list size.docx")
        val mock: ChapterParser = SimpleParser(Chapter(0), parser)
        for (p in 0 until 14) {
            mock.validateListElement(p)
        }
        errorAssert(parser.mistakes, LIST_LEVEL_MORE_THAN_2, LIST_LEVEL_MORE_THAN_2, ORDERED_LIST_WRONG_LETTER)
    }
}
