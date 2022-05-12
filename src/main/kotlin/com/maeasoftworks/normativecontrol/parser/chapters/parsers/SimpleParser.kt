package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.rules.*
import org.docx4j.wml.P

class SimpleParser(parser: DocumentParser, chapter: Chapter) : ChapterParser(parser, chapter) {
    override fun parse() {
        val paragraphs = chapter.content
        val headerPPr = resolver.getEffectivePPr((chapter[0] as P).pPr)
        findBasePRErrors(chapter.startPos, headerPPr, mainDocumentPart, headerPFunctions, headerRFunctions)
        findBasePRErrors(chapter.startPos, headerPPr, mainDocumentPart, pCommonFunctions, rCommonFunctions)
        for (paragraph in 1 until paragraphs.size) {
            val pPr = resolver.getEffectivePPr((chapter[paragraph] as P).pPr)
            findBasePRErrors(chapter.startPos + paragraph, pPr, mainDocumentPart, pCommonFunctions, rCommonFunctions)
            findBasePRErrors(
                chapter.startPos + paragraph,
                pPr,
                mainDocumentPart,
                regularPBeforeListCheckFunctions,
                regularRFunctions
            )
            if (pPr.numPr != null) {
                validateList(chapter.startPos + paragraph)
            } else {
                findBasePRErrors(
                    chapter.startPos + paragraph,
                    pPr,
                    mainDocumentPart,
                    regularPAfterListCheckFunctions,
                    listOf()
                )
            }
        }
    }

    companion object {
        private val pCommonFunctions =
            createPRulesCollection(
            BaseCommonPRules::commonPBackgroundCheck,
            BaseCommonPRules::commonPBorderCheck,
            BaseCommonPRules::commonPTextAlignCheck,
            BaseCommonPRules::commonPTextAlignCheck
        )

        private val rCommonFunctions =
            createRRulesCollection(
            BaseCommonRRules::commonRFontCheck,
            BaseCommonRRules::commonRFontSizeCheck,
            BaseCommonRRules::commonRItalicCheck,
            BaseCommonRRules::commonRStrikeCheck,
            BaseCommonRRules::commonRHighlightCheck,
            BaseCommonRRules::commonRColorCheck
        )

        private val headerRFunctions =
            createRRulesCollection(
                BaseHeaderRRules::headerRBoldCheck,
                BaseHeaderRRules::headerRUppercaseCheck
            )

        private val headerPFunctions =
            createPRulesCollection(
                BaseHeaderPRules::headerPJustifyCheck,
                BaseHeaderPRules::headerPLineSpacingCheck,
                BaseHeaderPRules::headerEmptyLineAfterHeaderExist,
                BaseHeaderPRules::headerPNotEndsWithDotCheck
            )

        private val regularPAfterListCheckFunctions =
            createPRulesCollection(
                BaseRegularPRules::regularPLeftIndentCheck,
                BaseRegularPRules::regularPRightIndentCheck,
                BaseRegularPRules::regularPFirstLineIndentCheck
            )

        private val regularPBeforeListCheckFunctions =
            createPRulesCollection(
                BaseRegularPRules::regularPJustifyCheck,
                BaseRegularPRules::regularPLineSpacingCheck
            )

        private val regularRFunctions =
            createRRulesCollection(
                BaseRegularRRules::regularRBoldCheck,
                BaseRegularRRules::regularRCapsCheck,
                BaseRegularRRules::regularRUnderlineCheck,
                BaseRegularRRules::regularRSpacingCheck
            )
    }
}