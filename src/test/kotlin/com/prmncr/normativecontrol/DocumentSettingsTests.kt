package com.prmncr.normativecontrol

import com.prmncr.normativecontrol.dtos.ErrorType
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
internal class DocumentSettingsTests : TestsBase() {
    @Test
    fun incorrectSizeTest() {
        val parser = createParser("document settings/incorrectWidth.docx")
        parser.checkPageSize()
        Assert.notEmpty(parser.errors, "Error not found!")
        Assert.state(
            parser.errors[0].errorType === ErrorType.INCORRECT_PAGE_SIZE
                    && parser.errors[0].paragraph.toInt() == -1
                    && parser.errors[0].run.toInt() == -1,
            "Wrong error!"
        )
    }

    @Test
    fun incorrectMarginTest() {
        val parser = createParser("document settings/incorrectMargin.docx")
        parser.checkPageMargins()
        Assert.notEmpty(parser.errors, "Error not found!")
        Assert.state(
            parser.errors[0].errorType === ErrorType.INCORRECT_PAGE_MARGINS
                    && parser.errors[0].paragraph.toInt() == -1
                    && parser.errors[0].run.toInt() == -1,
            "Wrong error!"
        )
    }
}