package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.rules.base.*
import com.maeasoftworks.normativecontrol.parser.chapters.rules.body.BodyHeaderPRules
import com.maeasoftworks.normativecontrol.parser.chapters.rules.body.BodyHeaderRRules
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.docx4j.TextUtils
import org.docx4j.wml.*
import javax.xml.bind.JAXBElement

class BodyParser(parser: DocumentParser, chapter: Chapter) : ChapterParser(parser, chapter) {

    override fun parse() {
        val headerPPr = resolver.getEffectivePPr((chapter[0] as P).pPr)
        val header = mainDocumentPart.content[chapter.startPos] as P
        val isEmpty = TextUtils.getText(header).isEmpty()
        findPErrors(chapter.startPos, headerPPr, isEmpty, headerPFunctions + commonPFunctions)
        for (r in 0 until header.content.size) {
            if (header.content[r] is R) {
                val rPr = resolver.getEffectiveRPr((header.content[r] as R).rPr, header.pPr)
                findRErrors(chapter.startPos, r, rPr, isEmpty, headerRFunctions + commonRFunctions)
            } else {
                handleNotRContent(chapter.startPos, r)
            }
        }
        for (p in chapter.startPos + 1 until chapter.startPos + chapter.content.size) {
            val pPr = resolver.getEffectivePPr((mainDocumentPart.content[p] as P).pPr)
            val paragraph = mainDocumentPart.content[p] as P
            val isEmptyP = TextUtils.getText(paragraph).isEmpty()
            findPErrors(p, pPr, isEmptyP, commonPFunctions + regularPBeforeListCheckFunctions)
            if (pPr.numPr != null) {
                validateList(p)
                continue
            }
            findPErrors(p, pPr, isEmptyP, regularPAfterListCheckFunctions)

            for (r in 0 until paragraph.content.size) {
                if (paragraph.content[r] is R) {
                    val rPr = resolver.getEffectiveRPr((paragraph.content[r] as R).rPr, paragraph.pPr)
                    findRErrors(p, r, rPr, isEmpty, commonRFunctions + regularRFunctions)
                } else {
                    handleNotRContent(p, r)
                }
            }
        }
    }

    override fun findPErrors(p: Int, pPr: PPr, isEmpty: Boolean, pFunctionWrappers: Iterable<PFunctionWrapper>) {
        for (wrapper in pFunctionWrappers) {
            val error = wrapper.function(document.id, p, pPr, isEmpty, mainDocumentPart)
            if (error != null) {
                errors += error
            }
        }
    }

    override fun findRErrors(
        p: Int,
        r: Int,
        rPr: RPr,
        isEmpty: Boolean,
        rFunctionWrappers: Iterable<RFunctionWrapper> ) {
        for (wrapper in rFunctionWrappers) {
            val error = wrapper.function(document.id, p, r, rPr, isEmpty, mainDocumentPart)
            if (error != null) {
                errors += error
            }
        }
    }

    override fun handleNotRContent(p: Int, r: Int) {
        val something = (mainDocumentPart.content[p] as P).content[r]
        if (something is ProofErr) {
            if (something.type == "gramStart") {
                errors += DocumentError(document.id, p, r + 1, ErrorType.WORD_GRAMMATICAL_ERROR)
            } else if (something.type == "spellStart") {
                errors += DocumentError(document.id, p, r + 1, ErrorType.WORD_SPELL_ERROR)
            }
        }
        if (something is JAXBElement<*>) {
            if (something.value is P.Hyperlink) {
                errors += DocumentError(document.id, p, r, ErrorType.TEXT_HYPERLINKS_NOT_ALLOWED_HERE)
            }
        }
    }

    companion object {
        private val commonPFunctions =
            createPRulesCollection(
                BaseCommonPRules::commonPBackgroundCheck,
                BaseCommonPRules::commonPBorderCheck,
                BaseCommonPRules::commonPTextAlignCheck,
                BaseCommonPRules::commonPTextAlignCheck
            )

        private val commonRFunctions =
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
                BaseHeaderRRules::headerRBoldCheck
            )

        private val headerPFunctions =
            createPRulesCollection(
                BodyHeaderPRules::headerPJustifyCheck,
                BodyHeaderPRules::headerPUppercaseCheck,
                BaseHeaderPRules::headerPLineSpacingCheck,
                BaseHeaderPRules::headerEmptyLineAfterHeaderExist,
                BaseHeaderPRules::headerPNotEndsWithDotCheck,
                BaseRegularPRules::regularPFirstLineIndentCheck,
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