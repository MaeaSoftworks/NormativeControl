package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.SimpleParser
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType.*
import org.junit.jupiter.api.Test

class HeaderTests : ParserTestFactory(HeaderTests::class) {
    @Test
    fun `header with correct style validated properly`() {
        val parserBase = createParser("correct header style.docx")
        parserBase.parsers += SimpleParser(parserBase, Chapter(0, parserBase.mainDocumentPart.content))
        parserBase.parsers[0].parse()
        errorAssert(parserBase.errors, WORD_SPELL_ERROR)
    }

    @Test
    fun `header with incorrect style validated properly`() {
        val parserBase = createParser("wrong header style.docx")
        parserBase.parsers += SimpleParser(parserBase, Chapter(0, parserBase.mainDocumentPart.content))
        parserBase.parsers[0].parse()
        errorAssert(
            parserBase.errors,
            TEXT_HEADER_ALIGNMENT,
            TEXT_HEADER_LINE_SPACING,
            TEXT_HEADER_NOT_BOLD,
            TEXT_HEADER_NOT_UPPERCASE,
            TEXT_COMMON_FONT,
            TEXT_COMMON_INCORRECT_FONT_SIZE,
            TEXT_COMMON_TEXT_COLOR,
            WORD_SPELL_ERROR
        )
    }
}