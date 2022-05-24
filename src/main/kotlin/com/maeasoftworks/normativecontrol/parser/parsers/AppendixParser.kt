package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.parser.model.Chapter
import org.docx4j.wml.P
import org.docx4j.wml.PPr

class AppendixParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {

    override fun parseHeader() {}

    override fun parseP(p: Int, pPr: PPr, isEmpty: Boolean) {}

    override fun parseR(p: Int, r: Int, paragraph: P) {}

    override fun handleHyperlink(p: Int, r: Int) {}
}