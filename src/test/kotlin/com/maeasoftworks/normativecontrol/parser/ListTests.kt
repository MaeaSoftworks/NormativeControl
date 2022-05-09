package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.ChapterParser
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
class ListTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "list"

    @Test
    fun `list borders found properly`() {
        val parser = base.createParser(directory, "list size.docx")
        val mock: ChapterParser = object : ChapterParser(parser, Chapter(0)) {
            override fun parse() {}
        }
        mock.validateList(0)
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }
}