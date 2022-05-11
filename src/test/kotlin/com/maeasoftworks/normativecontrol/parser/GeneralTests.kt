package com.maeasoftworks.normativecontrol.parser

import org.junit.jupiter.api.Test
import org.springframework.util.Assert

class GeneralTests : ParserTestFactory(GeneralTests::class) {

    @Test
    fun `annotation validated properly`() {
        val parser = createParser("full test 1.docx")
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()
        parser.createParsers()
        parser.parsers[1].parse()
        for (error in parser.errors) {
            println("${error.paragraphId} ${error.runId} ${error.errorType}")
        }
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun `introduction validated properly`() {
        val parser = createParser("full test 1.docx")
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()
        parser.createParsers()
        parser.parsers[3].parse()
        for (error in parser.errors) {
            println("${error.paragraphId} ${error.runId} ${error.errorType}")
        }
        Assert.isTrue(parser.errors.size != 0, "There should be errors!")
    }
}