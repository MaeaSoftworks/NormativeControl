package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.ChapterParser
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.PFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.RFunctionWrapper
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr
import org.junit.jupiter.api.Test
import org.springframework.util.Assert

class ListTests : ParserTestFactory(ListTests::class) {

    @Test
    fun `list borders found properly`() {
        val parser = createParser("list size.docx")
        val mock: ChapterParser = object : ChapterParser(parser, Chapter(0)) {
            override fun parse() {}
            override fun findPErrors(
                p: Int,
                pPr: PPr,
                isEmpty: Boolean,
                pFunctionWrappers: Iterable<PFunctionWrapper>
            ) {
                TODO("Not yet implemented")
            }

            override fun findRErrors(
                p: Int,
                r: Int,
                rPr: RPr,
                isEmpty: Boolean,
                rFunctionWrappers: Iterable<RFunctionWrapper>
            ) {
                TODO("Not yet implemented")
            }

            override fun handleNotRContent(p: Int, r: Int) {
                TODO("Not yet implemented")
            }
        }
        mock.validateList(0)
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }
}