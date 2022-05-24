package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.parser.Rules
import com.maeasoftworks.normativecontrol.parser.model.Chapter
import org.docx4j.TextUtils
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R

class TitleParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    override fun parseHeader() {}
    override fun handleHyperlink(p: Int, r: Int) {}

    override fun parse(context: ChapterParser) {
        for (p in chapter.startPos until chapter.startPos + chapter.content.size) {
            handleContent(p, context)
        }
    }

    override fun parseP(p: Int, pPr: PPr, isEmpty: Boolean) {
        val paragraph = mainDocumentPart.content[p] as P
        val isEmptyP = TextUtils.getText(paragraph).isBlank()
        applyPFunctions(p, pPr, isEmptyP, ChapterParser.createPRulesCollection(
            Rules.Default.PictureTitle.P::justifyIsCenter,
            Rules.Default.PictureTitle.P::hasNotDotInEnd
        ) + SimpleParser.pCommonFunctions)
    }

    override fun parseR(p: Int, r: Int, paragraph: P) {
        if (paragraph.content[r] is R) {
            val rPr = resolver.getEffectiveRPr((paragraph.content[r] as R).rPr, paragraph.pPr)
            applyRFunctions(p, r, rPr, TextUtils.getText(paragraph.content[r]).isBlank(),
                SimpleParser.rCommonFunctions
            )
        }
    }
}