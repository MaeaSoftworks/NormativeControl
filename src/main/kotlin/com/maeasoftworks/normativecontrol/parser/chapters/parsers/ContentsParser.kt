package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

class ContentsParser(parser: DocumentParser, chapter: Chapter) : ChapterParser(parser, chapter) {

    override fun parse() {

    }

    override fun findPErrors(p: Int, pPr: PPr, isEmpty: Boolean, pFunctionWrappers: Iterable<PFunctionWrapper>) {}

    override fun findRErrors(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, rFunctionWrappers: Iterable<RFunctionWrapper>) {}

    override fun handleNotRContent(p: Int, r: Int) {}
}