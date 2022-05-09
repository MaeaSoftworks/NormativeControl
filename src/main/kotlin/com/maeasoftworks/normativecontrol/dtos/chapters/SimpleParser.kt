package com.maeasoftworks.normativecontrol.dtos.chapters

import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.dtos.Chapter
import com.maeasoftworks.normativecontrol.dtos.DocumentParser
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

class SimpleParser(parser: DocumentParser, chapter: Chapter) : ChapterParser(parser, chapter) {
    companion object {
        private val pFunctions = arrayOf<(documentId: String,
                                          p: Int,
                                          isEmpty: Boolean,
                                          pPr: PPr) -> DocumentError?>(
            BasePRules::commonPBackgroundCheck,
            BasePRules::commonPBorderCheck,
            BasePRules::commonPTextAlignCheck,
            BasePRules::commonPTextAlignCheck
        )

        private val rFunctions = arrayOf<(documentId: String,
                                          rPr: RPr,
                                          p: Int,
                                          r: Int,
                                          isEmpty: Boolean) -> DocumentError?>(
            BaseRRules::commonRFontCheck,
            BaseRRules::commonRColorCheck,
            BaseRRules::commonRItalicCheck,
            BaseRRules::commonRStrikeCheck,
            BaseRRules::commonRHighlightCheck
        )
    }

    override fun parse() {
        val paragraphs = chapter.content
        findHeaderPRErrors(chapter.startPos)
        findCommonPRErrors(chapter.startPos, resolver.getEffectivePPr((chapter[0] as P).pPr), pFunctions, rFunctions)
        for (paragraph in 1 until paragraphs.size) {
            val pPr = resolver.getEffectivePPr((chapter[paragraph] as P).pPr)
            findRegularPRErrors(chapter.startPos + paragraph)
            findCommonPRErrors(paragraph, pPr, pFunctions, rFunctions)
        }
    }
}