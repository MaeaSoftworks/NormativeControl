package com.maeasoftworks.docx4nc

import com.maeasoftworks.docx4nc.enums.MistakeType
import com.maeasoftworks.docx4nc.enums.MistakeType.*
import com.maeasoftworks.docx4nc.model.Chapter
import com.maeasoftworks.docx4nc.parsers.SimpleParser
import org.junit.jupiter.api.Test

class StyleTests : ParserTestFactory(StyleTests::class) {
    @Test
    fun `incorrect page size validated properly`() {
        val parser = createParser("incorrect size.docx")
        parser.verifyPageSize()
        errorAssert(parser.mistakes, PAGE_WIDTH_IS_INCORRECT, PAGE_HEIGHT_IS_INCORRECT)
    }

    @Test
    fun `incorrect page margin validated properly`() {
        val parser = createParser("incorrect margin.docx")
        parser.verifyPageMargins()
        errorAssert(
            parser.mistakes,
            PAGE_MARGIN_TOP_IS_INCORRECT,
            PAGE_MARGIN_RIGHT_IS_INCORRECT,
            PAGE_MARGIN_BOTTOM_IS_INCORRECT,
            PAGE_MARGIN_LEFT_IS_INCORRECT
        )
    }

    @Test
    fun `incorrect header style validated properly`() {
        val parser = createParser("very broken text.docx")
        parser.parsers += SimpleParser(Chapter(0, parser.doc.content), parser)
        parser.parsers[0].parse()
        errorAssert(
            parser.mistakes,
            TEXT_COMMON_FONT,
            TEXT_COMMON_INCORRECT_FONT_SIZE,
            TEXT_COMMON_ITALIC_TEXT,
            TEXT_COMMON_STRIKETHROUGH,
            TEXT_COMMON_HIGHLIGHT,
            TEXT_COMMON_TEXT_COLOR,
            TEXT_COMMON_RUN_SPACING,
            TEXT_HEADER_ALIGNMENT,
            TEXT_HEADER_LINE_SPACING,
            CHAPTER_EMPTY,
            TEXT_HEADER_AUTO_HYPHEN,
            TEXT_COMMON_BACKGROUND_FILL,
            TEXT_COMMON_BORDER
        )
    }
}
