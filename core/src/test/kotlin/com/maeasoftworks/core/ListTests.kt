package com.maeasoftworks.core

import com.maeasoftworks.core.enums.MistakeType.LIST_LEVEL_MORE_THAN_2
import com.maeasoftworks.core.enums.MistakeType.ORDERED_LIST_WRONG_LETTER
import com.maeasoftworks.core.model.Chapter
import com.maeasoftworks.core.parsers.ChapterParser
import com.maeasoftworks.core.parsers.SimpleParser
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
