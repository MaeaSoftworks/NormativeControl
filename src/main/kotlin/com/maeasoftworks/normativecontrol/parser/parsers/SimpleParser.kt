package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.Rules
import com.maeasoftworks.normativecontrol.parser.model.Chapter
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.docx4j.TextUtils
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R

class SimpleParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    override fun parseHeader() {
        val headerPPr = resolver.getEffectivePPr((chapter[0] as P).pPr)
        val header = mainDocumentPart.content[chapter.startPos] as P
        val isEmpty = TextUtils.getText(header).isBlank()
        applyPFunctions(chapter.startPos, headerPPr, isEmpty, headerPFunctions + pCommonFunctions)
        for (r in 0 until header.content.size) {
            if (header.content[r] is R) {
                val rPr = resolver.getEffectiveRPr((header.content[r] as R).rPr, header.pPr)
                applyRFunctions(chapter.startPos, r, rPr, isEmpty, headerRFunctions + rCommonFunctions)
            } else {
                handlePContent(chapter.startPos, r, this)
            }
        }
    }

    override fun parseP(p: Int, pPr: PPr, isEmpty: Boolean) {
        val paragraph = mainDocumentPart.content[p] as P
        val isEmptyP = TextUtils.getText(paragraph).isBlank()
        applyPFunctions(p, pPr, isEmptyP, pCommonFunctions + regularPBeforeListCheckFunctions)
        if (pPr.numPr != null) {
            validateList(p)
            return
        }
        applyPFunctions(p, pPr, isEmptyP, regularPAfterListCheckFunctions)
    }

    override fun parseR(p: Int, r: Int, paragraph: P) {
        if (paragraph.content[r] is R) {
            val rPr = resolver.getEffectiveRPr((paragraph.content[r] as R).rPr, paragraph.pPr)
            applyRFunctions(
                p,
                r,
                rPr,
                TextUtils.getText(paragraph.content[r]).isBlank(),
                rCommonFunctions + regularRFunctions
            )
        } else {
            handlePContent(p, r, this)
        }
    }

    override fun handleHyperlink(p: Int, r: Int) {
        errors += DocumentError(document.id, p, r, ErrorType.TEXT_HYPERLINKS_NOT_ALLOWED_HERE)
    }

    companion object {
        val pCommonFunctions =
            createPRulesCollection(
                Rules.Default.Common.P::hasNotBackground,
                Rules.Default.Common.P::notBordered
            )

        val rCommonFunctions =
            createRRulesCollection(
                Rules.Default.Common.R::isTimesNewRoman,
                Rules.Default.Common.R::fontSizeIs14,
                Rules.Default.Common.R::notItalic,
                Rules.Default.Common.R::notCrossedOut,
                Rules.Default.Common.R::notHighlighted,
                Rules.Default.Common.R::isBlack,
                Rules.Default.Common.R::letterSpacingIs0
            )

        val headerRFunctions =
            createRRulesCollection(
                Rules.Default.Header.R::isBold,
                Rules.Default.Header.R::isUppercase
            )

        val headerPFunctions =
            createPRulesCollection(
                Rules.Default.Header.P::justifyIsCenter,
                Rules.Default.Header.P::lineSpacingIsOne,
                Rules.Default.Header.P::emptyLineAfterHeaderExists,
                Rules.Default.Header.P::hasNotDotInEnd
            )

        val regularPAfterListCheckFunctions =
            createPRulesCollection(
                Rules.Default.RegularText.P::leftIndentIs0,
                Rules.Default.RegularText.P::rightIndentIs0,
                Rules.Default.RegularText.P::firstLineIndentIs1dot25
            )

        val regularPBeforeListCheckFunctions =
            createPRulesCollection(
                Rules.Default.RegularText.P::justifyIsBoth,
                Rules.Default.RegularText.P::lineSpacingIsOneAndHalf
            )

        val regularRFunctions =
            createRRulesCollection(
                Rules.Default.RegularText.R::isNotBold,
                Rules.Default.RegularText.R::isNotCaps,
                Rules.Default.RegularText.R::isUnderline
            )
    }
}