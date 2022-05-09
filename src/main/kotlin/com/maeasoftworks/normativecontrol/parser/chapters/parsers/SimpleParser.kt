package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.rules.*
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.RPr

class SimpleParser(parser: DocumentParser, chapter: Chapter) : ChapterParser(parser, chapter) {
    companion object {
        private val pCommonFunctions = arrayOf<(
            documentId: String,
            p: Int,
            isEmpty: Boolean,
            pPr: PPr,
            mainDocumentPart: MainDocumentPart
        ) -> DocumentError?>(
            BaseCommonPRules::commonPBackgroundCheck,
            BaseCommonPRules::commonPBorderCheck,
            BaseCommonPRules::commonPTextAlignCheck,
            BaseCommonPRules::commonPTextAlignCheck
        )

        private val rCommonFunctions = arrayOf<(
            documentId: String,
            rPr: RPr,
            p: Int,
            r: Int,
            isEmpty: Boolean,
            mainDocumentPart: MainDocumentPart
        ) -> DocumentError?>(
            BaseCommonRRules::commonRFontCheck,
            BaseCommonRRules::commonRFontSizeCheck,
            BaseCommonRRules::commonRItalicCheck,
            BaseCommonRRules::commonRStrikeCheck,
            BaseCommonRRules::commonRHighlightCheck,
            BaseCommonRRules::commonRColorCheck
        )

        private val headerRFunctions =
            arrayOf<(documentId: String, rPr: RPr, p: Int, r: Int, isEmpty: Boolean, mainDocumentPart: MainDocumentPart) -> DocumentError?>(
                BaseHeaderRRules::headerRBoldCheck,
                BaseHeaderRRules::headerRUppercaseCheck
            )

        private val headerPFunctions =
            arrayOf<(documentId: String, p: Int, isEmpty: Boolean, pPr: PPr, mainDocumentPart: MainDocumentPart) -> DocumentError?>(
                BaseHeaderPRules::headerPJustifyCheck,
                BaseHeaderPRules::headerPLineSpacingCheck,
                BaseHeaderPRules::headerEmptyLineAfterHeaderExist,
                BaseHeaderPRules::headerPNotEndsWithDotCheck
            )

        private val regularPAfterListCheckFunctions =
            arrayOf<(documentId: String, p: Int, isEmpty: Boolean, pPr: PPr, mainDocumentPart: MainDocumentPart) -> DocumentError?>(
                BaseRegularPRules::regularPLeftIndentCheck,
                BaseRegularPRules::regularPRightIndentCheck,
                BaseRegularPRules::regularPFirstLineIndentCheck
            )

        private val regularPBeforeListCheckFunctions =
            arrayOf<(documentId: String, p: Int, isEmpty: Boolean, pPr: PPr, mainDocumentPart: MainDocumentPart) -> DocumentError?>(
                BaseRegularPRules::regularPJustifyCheck,
                BaseRegularPRules::regularPLineSpacingCheck
            )

        private val regularRFunctions =
            arrayOf<(documentId: String, rPr: RPr, p: Int, r: Int, isEmpty: Boolean, mainDocumentPart: MainDocumentPart) -> DocumentError?>(
                BaseRegularRRules::regularRBoldCheck,
                BaseRegularRRules::regularRCapsCheck,
                BaseRegularRRules::regularRUnderlineCheck,
                BaseRegularRRules::regularRSpacingCheck
            )
    }

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
                    arrayOf()
                )
            }
        }
    }
}