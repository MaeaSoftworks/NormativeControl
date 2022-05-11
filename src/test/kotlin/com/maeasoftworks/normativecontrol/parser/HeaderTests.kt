package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.ChapterParser
import com.maeasoftworks.normativecontrol.parser.chapters.rules.BaseCommonPRules
import com.maeasoftworks.normativecontrol.parser.chapters.rules.BaseCommonRRules
import com.maeasoftworks.normativecontrol.parser.chapters.rules.BaseHeaderPRules
import com.maeasoftworks.normativecontrol.parser.chapters.rules.BaseHeaderRRules
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.docx4j.wml.P
import org.junit.jupiter.api.Test

internal class HeaderTests : ParserTestFactory(HeaderTests::class) {
    @Test
    fun `header with correct style validated properly`() {
        val parserBase = createParser("correct header style.docx")
        parserBase.parsers += object : ChapterParser(parserBase, Chapter(0, parserBase.mainDocumentPart.content)) {
            override fun parse() {
                val headerPPr = resolver.getEffectivePPr((chapter[0] as P).pPr)
                findBasePRErrors(
                    chapter.startPos, headerPPr, mainDocumentPart,
                    arrayOf(
                        BaseHeaderPRules::headerPJustifyCheck,
                        BaseHeaderPRules::headerPLineSpacingCheck,
                        BaseHeaderPRules::headerEmptyLineAfterHeaderExist,
                        BaseHeaderPRules::headerPNotEndsWithDotCheck
                    ),
                    arrayOf(
                        BaseHeaderRRules::headerRBoldCheck,
                        BaseHeaderRRules::headerRUppercaseCheck
                    )
                )
                findBasePRErrors(
                    chapter.startPos, headerPPr, mainDocumentPart,
                    arrayOf(
                        BaseCommonPRules::commonPBackgroundCheck,
                        BaseCommonPRules::commonPBorderCheck,
                        BaseCommonPRules::commonPTextAlignCheck,
                        BaseCommonPRules::commonPTextAlignCheck
                    ),
                    arrayOf(
                        BaseCommonRRules::commonRFontCheck,
                        BaseCommonRRules::commonRFontSizeCheck,
                        BaseCommonRRules::commonRItalicCheck,
                        BaseCommonRRules::commonRStrikeCheck,
                        BaseCommonRRules::commonRHighlightCheck,
                        BaseCommonRRules::commonRColorCheck
                    )
                )
            }
        }
        parserBase.parsers[0].parse()
        errorAssert(parserBase.errors)
    }

    @Test
    fun `header with incorrect style validated properly`() {
        val parserBase = createParser("wrong header style.docx")
        parserBase.parsers += object : ChapterParser(parserBase, Chapter(0, parserBase.mainDocumentPart.content)) {
            override fun parse() {
                val headerPPr = resolver.getEffectivePPr((chapter[0] as P).pPr)
                findBasePRErrors(
                    chapter.startPos, headerPPr, mainDocumentPart,
                    arrayOf(
                        BaseHeaderPRules::headerPJustifyCheck,
                        BaseHeaderPRules::headerPLineSpacingCheck,
                        BaseHeaderPRules::headerEmptyLineAfterHeaderExist,
                        BaseHeaderPRules::headerPNotEndsWithDotCheck
                    ),
                    arrayOf(
                        BaseHeaderRRules::headerRBoldCheck,
                        BaseHeaderRRules::headerRUppercaseCheck
                    )
                )
                findBasePRErrors(
                    chapter.startPos, headerPPr, mainDocumentPart,
                    arrayOf(
                        BaseCommonPRules::commonPBackgroundCheck,
                        BaseCommonPRules::commonPBorderCheck,
                        BaseCommonPRules::commonPTextAlignCheck,
                        BaseCommonPRules::commonPTextAlignCheck
                    ),
                    arrayOf(
                        BaseCommonRRules::commonRFontCheck,
                        BaseCommonRRules::commonRFontSizeCheck,
                        BaseCommonRRules::commonRItalicCheck,
                        BaseCommonRRules::commonRStrikeCheck,
                        BaseCommonRRules::commonRHighlightCheck,
                        BaseCommonRRules::commonRColorCheck
                    )
                )
            }
        }
        parserBase.parsers[0].parse()
        errorAssert(
            parserBase.errors,
            ErrorType.TEXT_HEADER_ALIGNMENT,
            ErrorType.TEXT_HEADER_LINE_SPACING,
            ErrorType.TEXT_HEADER_NOT_BOLD,
            ErrorType.TEXT_HEADER_NOT_UPPERCASE,
            ErrorType.TEXT_COMMON_FONT,
            ErrorType.TEXT_COMMON_INCORRECT_FONT_SIZE,
            ErrorType.TEXT_COMMON_TEXT_COLOR
        )
    }
}