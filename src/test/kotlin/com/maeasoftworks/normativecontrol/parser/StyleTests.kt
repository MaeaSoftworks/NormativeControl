package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.enums.MistakeType
import com.maeasoftworks.normativecontrol.parser.model.Chapter
import com.maeasoftworks.normativecontrol.parser.parsers.SimpleParser
import org.junit.jupiter.api.Test

class StyleTests : ParserTestFactory(StyleTests::class) {
    @Test
    fun `incorrect page size validated properly`() {
        val parser = createParser("incorrect size.docx")
        parser.verifyPageSize()
        errorAssert(parser.mistakes, MistakeType.PAGE_WIDTH_IS_INCORRECT, MistakeType.PAGE_HEIGHT_IS_INCORRECT)
    }

    @Test
    fun `incorrect page margin validated properly`() {
        val parser = createParser("incorrect margin.docx")
        parser.verifyPageMargins()
        errorAssert(
            parser.mistakes,
            MistakeType.PAGE_MARGIN_TOP_IS_INCORRECT,
            MistakeType.PAGE_MARGIN_RIGHT_IS_INCORRECT,
            MistakeType.PAGE_MARGIN_BOTTOM_IS_INCORRECT,
            MistakeType.PAGE_MARGIN_LEFT_IS_INCORRECT
        )
    }

    @Test
    fun `incorrect header style validated properly`() {
        val parser = createParser("very broken text.docx")
        parser.parsers += SimpleParser(Chapter(0, parser.mainDocumentPart.content), parser)
        parser.parsers[0].parse()
        errorAssert(
            parser.mistakes,
            MistakeType.TEXT_HEADER_NOT_UPPERCASE,
            MistakeType.TEXT_COMMON_FONT,
            MistakeType.TEXT_COMMON_INCORRECT_FONT_SIZE,
            MistakeType.TEXT_COMMON_ITALIC_TEXT,
            MistakeType.TEXT_COMMON_STRIKETHROUGH,
            MistakeType.TEXT_COMMON_HIGHLIGHT,
            MistakeType.TEXT_COMMON_TEXT_COLOR,
            MistakeType.TEXT_HEADER_ALIGNMENT,
            MistakeType.TEXT_HEADER_LINE_SPACING,
            MistakeType.CHAPTER_EMPTY,
            MistakeType.TEXT_COMMON_BACKGROUND_FILL,
            MistakeType.TEXT_COMMON_BORDER
        )
    }
}