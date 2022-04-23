package com.prmncr.normativecontrol.parser

import com.prmncr.normativecontrol.dtos.ErrorType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
internal class HeaderTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "headers"

    @Test
    fun headerIsCorrect() {
        val parser = base.createParser(directory, "correctHeaderStyle.docx")
        parser.findHeaderAllErrors(0)
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun headerIsIncorrect() {
        val parser = base.createParser(directory, "wrongHeaderStyle.docx")
        parser.findHeaderAllErrors(0)
        Assert.isTrue(parser.errors.size == 5, "There should be errors!")
        Assert.state(
            parser.errors[0].errorType == ErrorType.INCORRECT_HEADER_ALIGNMENT && parser.errors[0].paragraph == 0 && parser.errors[0].run == 0,
            "Wrong error!"
        )
        Assert.state(
            parser.errors[1].errorType == ErrorType.HEADER_IS_NOT_UPPERCASE && parser.errors[0].paragraph == 0 && parser.errors[0].run == 0,
            "Wrong error!"
        )
        Assert.state(
            parser.errors[2].errorType == ErrorType.INCORRECT_TEXT_FONT && parser.errors[0].paragraph == 0 && parser.errors[0].run == 0,
            "Wrong error!"
        )
        Assert.state(
            parser.errors[3].errorType == ErrorType.INCORRECT_TEXT_COLOR && parser.errors[0].paragraph == 0 && parser.errors[0].run == 0,
            "Wrong error!"
        )
        Assert.state(
            parser.errors[4].errorType == ErrorType.INCORRECT_FONT_SIZE && parser.errors[0].paragraph == 0 && parser.errors[0].run == 0,
            "Wrong error!"
        )
    }
}