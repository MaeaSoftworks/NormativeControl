package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.model.Chapter
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

class ReferencesParser(parser: DocumentParser, chapter: Chapter) : ChapterParser(parser, chapter) {

    override fun parseHeader() {}

    override fun parseP(p: Int, pPr: PPr, isEmpty: Boolean) {}

    override fun parseR(p: Int, r: Int, paragraph: P) {}

    override fun handleHyperlink(p: Int, r: Int) {}

    override fun applyPFunctions(p: Int, pPr: PPr, isEmpty: Boolean, pFunctionWrappers: Iterable<PFunctionWrapper>) {}

    override fun applyRFunctions(
        p: Int,
        r: Int,
        rPr: RPr,
        isEmpty: Boolean,
        rFunctionWrappers: Iterable<RFunctionWrapper>
    ) {
    }

    override fun handlePContent(p: Int, r: Int, parser: ChapterParser) {}
}