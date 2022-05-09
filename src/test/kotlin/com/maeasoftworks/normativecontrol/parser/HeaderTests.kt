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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.Assert

@SpringBootTest
internal class HeaderTests {
    @Autowired
    lateinit var base: TestBase
    private val directory: String = "header"

    @Test
    fun `header with correct style validated properly`() {
        val parserBase = base.createParser(directory, "correctHeaderStyle.docx")
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
        Assert.isTrue(parserBase.errors.size == 0, "There shouldn't be any error!")
    }

    @Test
    fun `header with incorrect style validated properly`() {
        val parserBase = base.createParser(directory, "wrongHeaderStyle.docx")
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
        Assert.isTrue(parserBase.errors.size == 7, "There should be 7 errors!")
        Assert.state(parserBase.errors[0].errorType == ErrorType.TEXT_HEADER_ALIGNMENT, "Wrong error!")
        Assert.state(parserBase.errors[1].errorType == ErrorType.TEXT_HEADER_LINE_SPACING, "Wrong error!")
        Assert.state(parserBase.errors[2].errorType == ErrorType.TEXT_HEADER_NOT_BOLD, "Wrong error!")
        Assert.state(parserBase.errors[3].errorType == ErrorType.TEXT_HEADER_NOT_UPPERCASE, "Wrong error!")
        Assert.state(parserBase.errors[4].errorType == ErrorType.TEXT_COMMON_FONT, "Wrong error!")
        Assert.state(parserBase.errors[5].errorType == ErrorType.TEXT_COMMON_INCORRECT_FONT_SIZE, "Wrong error!")
        Assert.state(parserBase.errors[6].errorType == ErrorType.TEXT_COMMON_TEXT_COLOR, "Wrong error!")
    }
}