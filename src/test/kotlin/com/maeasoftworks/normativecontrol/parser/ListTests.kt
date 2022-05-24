package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.chapters.model.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.ChapterParser
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.PFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.RFunctionWrapper
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr
import org.junit.jupiter.api.Test
import org.springframework.util.Assert

class ListTests : ParserTestFactory(ListTests::class) {

    @Test
    fun `list borders found properly`() {
        val parser = createParser("list size.docx")
        val mock: ChapterParser = object : ChapterParser(parser, Chapter(0)) {
            override fun parseHeader() {}
            override fun parseP(p: Int, pPr: PPr, isEmpty: Boolean) {}
            override fun parseR(p: Int, r: Int, paragraph: P) {}
            override fun applyPFunctions(p: Int, pPr: PPr, isEmpty: Boolean, pFunctionWrappers: Iterable<PFunctionWrapper>) {}
            override fun applyRFunctions(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, rFunctionWrappers: Iterable<RFunctionWrapper>) {}
            override fun handlePContent(p: Int, r: Int, parser: ChapterParser) {}
            override fun handleHyperlink(p: Int, r: Int) {}
        }
        mock.validateList(0)
        Assert.isTrue(parser.errors.size == 0, "There shouldn't be any error!")
    }
}