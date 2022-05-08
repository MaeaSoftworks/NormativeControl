package com.maeasoftworks.normativecontrol.parser

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
        parser.findHeaderAllErrors(0)
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun `header with incorrect style validated properly`() {
        val parser = base.createParser(directory, "wrongHeaderStyle.docx")
        parser.findHeaderAllErrors(0)
        Assert.isTrue(parser.errors.size == 5, "There should be errors!")
        Assert.state(
            parser.errors[0].errorType == ErrorType.TEXT_HEADER_ALIGNMENT && parser.errors[0].paragraphId == 0 && parser.errors[0].runId == 0,
            "Wrong error!"
        )
        Assert.state(
            parser.errors[1].errorType == ErrorType.TEXT_HEADER_NOT_UPPERCASE && parser.errors[0].paragraphId == 0 && parser.errors[0].runId == 0,
            "Wrong error!"
        )
        Assert.state(
            parser.errors[2].errorType == ErrorType.TEXT_COMMON_FONT && parser.errors[0].paragraphId == 0 && parser.errors[0].runId == 0,
            "Wrong error!"
        )
        Assert.state(
            parser.errors[3].errorType == ErrorType.TEXT_COMMON_INCORRECT_COLOR && parser.errors[0].paragraphId == 0 && parser.errors[0].runId == 0,
            "Wrong error!"
        )
        Assert.state(
            parser.errors[4].errorType == ErrorType.TEXT_COMMON_INCORRECT_FONT_SIZE && parser.errors[0].paragraphId == 0 && parser.errors[0].runId == 0,
            "Wrong error!"
        )
    }
}