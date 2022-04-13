package com.prmncr.normativecontrol

import com.prmncr.normativecontrol.dtos.ErrorType
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
internal class HeaderTests : TestsBase() {
    @Test
    fun headerDetectedWithoutLineBreak() {
        val parser = createParser("headers/headerWithoutLineBreak.docx")
        parser.findSectors()
        Assert.notEmpty(parser.sectors[0], "0 not found!")
        Assert.notEmpty(parser.sectors[1], "1 not found!")
    }

    @Test
    fun headerDetectedWithLineBreak() {
        val parser = createParser("headers/headerWithLineBreak.docx")
        parser.findSectors()
        Assert.notEmpty(parser.sectors[0], "0 not found!")
        Assert.notEmpty(parser.sectors[1], "1 not found!")
    }

    @Test
    fun headerIsCorrect() {
        val parser = createParser("headers/correctHeaderStyle.docx")
        parser.checkHeaderStyle(0)
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun headerIsIncorrect() {
        val parser = createParser("headers/invalidHeaders.docx")
        parser.checkHeaderStyle(0)
        parser.checkHeaderStyle(1)
        parser.checkHeaderStyle(2)
        parser.checkHeaderStyle(3)
        Assert.isTrue(parser.errors.size == 4, "There should be 4 errors!")
        Assert.isTrue(parser.errors[0].errorType === ErrorType.INCORRECT_TEXT_COLOR, "Color not detected!")
        Assert.isTrue(parser.errors[1].errorType === ErrorType.INCORRECT_HEADER_FONT, "Font not detected!")
        Assert.isTrue(parser.errors[2].errorType === ErrorType.INCORRECT_FONT_SIZE, "Font size not detected!")
        Assert.isTrue(parser.errors[3].errorType === ErrorType.INCORRECT_HEADER_ALIGNMENT, "Alignment not detected!")
    }

    @Test
    fun defaultStylesNotThrowError() {
        val parser = createParser("headers/invalidHeadersDefaults.docx")
        parser.checkHeaderStyle(0)
        parser.checkHeaderStyle(1)
        parser.checkHeaderStyle(2)
        parser.checkHeaderStyle(3)
        Assert.isTrue(parser.errors.size == 4, "There should be 4 errors!")
        Assert.isTrue(parser.errors[0].errorType === ErrorType.INCORRECT_TEXT_COLOR, "Color not detected!")
        Assert.isTrue(parser.errors[1].errorType === ErrorType.INCORRECT_HEADER_FONT, "Font not detected!")
        Assert.isTrue(parser.errors[2].errorType === ErrorType.INCORRECT_FONT_SIZE, "Font size not detected!")
        Assert.isTrue(parser.errors[3].errorType === ErrorType.INCORRECT_HEADER_ALIGNMENT, "Alignment not detected!")
    }
}