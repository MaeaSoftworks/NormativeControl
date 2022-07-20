package com.maeasoftworks.polonium

import com.maeasoftworks.polonium.enums.MistakeType.LIST_LEVEL_MORE_THAN_2
import com.maeasoftworks.polonium.enums.MistakeType.ORDERED_LIST_WRONG_LETTER
import com.maeasoftworks.polonium.model.Chapter
import com.maeasoftworks.polonium.parsers.ChapterParser
import com.maeasoftworks.polonium.parsers.SimpleParser
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
