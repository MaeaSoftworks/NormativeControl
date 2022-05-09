package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.dtos.Chapter
import com.maeasoftworks.normativecontrol.dtos.chapters.ChapterParser
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
internal class HeaderTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "header"

    @Test
    fun `header with correct style validated properly`() {
        val parser = base.createParser(directory, "correctHeaderStyle.docx")
        val mock: ChapterParser = object : ChapterParser(parser, Chapter(0)) {
            override fun parse() {}
        }
        mock.findHeaderPRErrors(0)
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun `header with incorrect style validated properly`() {
        val parser = base.createParser(directory, "wrongHeaderStyle.docx")
        val mock: ChapterParser = object : ChapterParser(parser, Chapter(0)) {
            override fun parse() {}
        }
        mock.findHeaderPRErrors(0)
        Assert.isTrue(parser.errors.size == 7, "There should be 6 errors!")
        Assert.state(parser.errors[0].errorType == ErrorType.TEXT_HEADER_ALIGNMENT, "Wrong error!")
        Assert.state(parser.errors[1].errorType == ErrorType.TEXT_HEADER_LINE_SPACING, "Wrong error!")
        Assert.state(parser.errors[2].errorType == ErrorType.TEXT_HEADER_NOT_UPPERCASE, "Wrong error!")
        Assert.state(parser.errors[3].errorType == ErrorType.TEXT_HEADER_NOT_BOLD, "Wrong error!")
        Assert.state(parser.errors[4].errorType == ErrorType.TEXT_COMMON_FONT, "Wrong error!")
        Assert.state(parser.errors[5].errorType == ErrorType.TEXT_COMMON_TEXT_COLOR, "Wrong error!")
        Assert.state(parser.errors[6].errorType == ErrorType.TEXT_COMMON_INCORRECT_FONT_SIZE, "Wrong error!")
    }
}