package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.SimpleParser
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.junit.jupiter.api.Test

class StyleTests : ParserTestFactory(StyleTests::class) {
    @Test
    fun `incorrect page size validated properly`() {
        val parser = createParser("incorrect size.docx")
        parser.verifyPageSize()
        errorAssert(parser.errors, ErrorType.PAGE_WIDTH_IS_INCORRECT, ErrorType.PAGE_HEIGHT_IS_INCORRECT)
    }

    @Test
    fun `incorrect page margin validated properly`() {
        val parser = createParser("incorrect margin.docx")
        parser.verifyPageMargins()
        errorAssert(
            parser.errors,
            ErrorType.PAGE_MARGIN_TOP_IS_INCORRECT,
            ErrorType.PAGE_MARGIN_RIGHT_IS_INCORRECT,
            ErrorType.PAGE_MARGIN_BOTTOM_IS_INCORRECT,
            ErrorType.PAGE_MARGIN_LEFT_IS_INCORRECT
        )
    }

    @Test
    fun `incorrect header style validated properly`() {
        val parser = createParser("very broken text.docx")
        parser.parsers += SimpleParser(parser, Chapter(0, parser.mainDocumentPart.content))
        parser.parsers[0].parse()
        errorAssert(
            parser.errors,
            ErrorType.TEXT_HEADER_ALIGNMENT,
            ErrorType.TEXT_HEADER_LINE_SPACING,
            ErrorType.CHAPTER_EMPTY,
            ErrorType.TEXT_COMMON_BACKGROUND_FILL,
            ErrorType.TEXT_COMMON_BORDER,
            ErrorType.TEXT_HEADER_NOT_UPPERCASE,
            ErrorType.TEXT_COMMON_FONT,
            ErrorType.TEXT_COMMON_INCORRECT_FONT_SIZE,
            ErrorType.TEXT_COMMON_ITALIC_TEXT,
            ErrorType.TEXT_COMMON_STRIKETHROUGH,
            ErrorType.TEXT_COMMON_HIGHLIGHT,
            ErrorType.TEXT_COMMON_TEXT_COLOR
        )
    }
}