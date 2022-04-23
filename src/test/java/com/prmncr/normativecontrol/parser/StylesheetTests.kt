package com.prmncr.normativecontrol.parser

import com.prmncr.normativecontrol.dtos.ErrorType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
internal class StylesheetTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "stylesheet"

    @Test
    fun incorrectSizeTest() {
        val parser = base.createParser(directory, "incorrectWidth.docx")
        parser.checkPageSize()
        Assert.notEmpty(parser.errors, "Error not found!")
        Assert.state(
            parser.errors[0].errorType == ErrorType.INCORRECT_PAGE_SIZE && parser.errors[0].paragraph == -1 && parser.errors[0].run == -1,
            "Wrong error!"
        )
    }

    @Test
    fun incorrectMarginTest() {
        val parser = base.createParser(directory, "incorrectMargin.docx")
        parser.checkPageMargins()
        Assert.notEmpty(parser.errors, "Error not found!")
        Assert.state(
            parser.errors[0].errorType == ErrorType.INCORRECT_PAGE_MARGINS && parser.errors[0].paragraph == -1 && parser.errors[0].run == -1,
            "Wrong error!"
        )
    }

    @Test
    fun headerIsCorrect() {
        val parser = base.createParser(directory, "overwrittenDefaultStyle.docx")
        parser.findHeaderAllErrors(0)
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun foundAllErrors() {
        val parser = base.createParser(directory, "veryWrongText.docx")
        parser.findGeneralAllErrors(0)
        Assert.isTrue(parser.errors.size > 0, "There should be errors!")
    }
}