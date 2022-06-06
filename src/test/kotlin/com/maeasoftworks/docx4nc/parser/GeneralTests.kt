package com.maeasoftworks.docx4nc.parser

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
        for (error in parser.mistakes) {
            println("${error.p} ${error.r} ${error.mistakeType} ${error.description}")
        }
        Assert.isTrue(parser.mistakes.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun `introduction validated properly`() {
        val parser = createParser("full test 1.docx")
        parser.findChapters()
        parser.detectChapters()
        parser.verifyChapters()
        parser.createParsers()
        parser.parsers[3].parse()
        for (error in parser.mistakes) {
            println("${error.p} ${error.r} ${error.mistakeType} ${error.description}")
        }
        Assert.isTrue(parser.mistakes.size != 0, "There should be errors!")
    }

    @Test
    fun `body validated properly`() {
        val parser = createParser("full test 1.docx")
        parser.setupChapters()
        parser.createParsers()
        parser.parsers[4].parse()
        parser.parsers[5].parse()
        parser.parsers[6].parse()
        parser.parsers[7].parse()
        for (error in parser.mistakes.filter { !it.mistakeType.name.contains("WHITESPACE") }) {
            println("${error.p} ${error.r} ${error.mistakeType} ${error.description}")
        }
        Assert.isTrue(parser.mistakes.size != 0, "There should be errors!")
    }

    @Test
    fun `contents validated properly`() {
        val parser = createParser("full test 1.docx")
        parser.setupChapters()
        parser.createParsers()
        parser.parsers[2].parse()
        for (error in parser.mistakes.filter { !it.mistakeType.name.contains("WHITESPACE") }) {
            println("${error.p} ${error.r} ${error.mistakeType} ${error.description}")
        }
        Assert.isTrue(parser.mistakes.size != 0, "There should be errors!")
    }

    @Test
    fun `full document validated properly`() {
        val parser = createParser("full test 1.docx")
        parser.setupChapters()
        parser.createParsers()
        parser.runVerification()
        for (error in parser.mistakes.filter { !it.mistakeType.name.contains("WHITESPACE") }) {
            println("${error.p} ${error.r} ${error.mistakeType} ${error.description}")
        }
        Assert.isTrue(parser.mistakes.size != 0, "There should be errors!")
    }
}