package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.rules.base.*
import com.maeasoftworks.normativecontrol.parser.chapters.rules.body.BodyHeaderPRules
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.docx4j.TextUtils
import org.docx4j.wml.*
import javax.xml.bind.JAXBElement

class BodyParser(parser: DocumentParser, chapter: Chapter) : ChapterParser(parser, chapter) {
    private val subchapters = Subchapter(
        chapter.startPos + 1,
        null,
        null,
        Regex("^(?:\\d\\.?){1,3}").find(TextUtils.getText(chapter.header))?.value?.removeSuffix(".")!!.toInt(), 1
    )

    /* P
    * JAXBElement <CTMarkup
    * RunIns JAXBElement <CTMarkup
    * JAXBElement <CTRel
    * JAXBElement <CTMoveBookmark
    * JAXBElement <CTMarkup
    * JAXBElement <RunTrackChange
    * JAXBElement <CTTrackChange
    * JAXBElement <CTTrackChange
    * JAXBElement <CTSimpleField
    * JAXBElement <RangePermissionStart
    * JAXBElement <RunTrackChange
    * JAXBElement <CTMoveToRangeEnd
    * JAXBElement <CTTrackChange
    * ProofErr
    * CommentRangeEnd
    * JAXBElement <CTMoveBookmark
    * R
    * RunDel
    * JAXBElement <CTMarkupRange
    * JAXBElement <CTSmartTagRun
    * JAXBElement <CTOMathPara
    * JAXBElement <CTPerm
    * CommentRangeStart
    * JAXBElement <CTMoveFromRangeEnd
    * JAXBElement <CTOMath
    * JAXBElement <CTCustomXmlRun
    * JAXBElement <CTTrackChange
    * JAXBElement <CTMarkup
    * JAXBElement <SdtRun
    * JAXBElement <P.Hyperlink
    * JAXBElement <CTBookmark
    * JAXBElement <P.Bdo
    * JAXBElement <P.Dir
    */


    /* R
    *  JAXBElement -> R.Ptab
    *  JAXBElement -> R.YearLong
    *  JAXBElement -> R.DayShort
    *  JAXBElement -> R.NoBreakHyphen
    *  JAXBElement -> R.EndnoteRef
    *  JAXBElement -> R.PgNum
    *  JAXBElement -> R.SoftHyphen
    *  JAXBElement -> Pict
    *  JAXBElement -> R.Tab
    *  JAXBElement -> Drawing
    *  Br
    *  JAXBElement -> R.Separator
    *  JAXBElement -> R.LastRenderedPageBreak
    *  JAXBElement -> Text
    *  JAXBElement -> CTObject
    *  JAXBElement -> CTFtnEdnRef
    *  JAXBElement -> Text
    *  JAXBElement -> CTFtnEdnRef
    *  JAXBElement -> R.MonthShort
    *  JAXBElement -> R.MonthLong
    *  JAXBElement -> R.ContinuationSeparator
    *  JAXBElement -> CTRuby
    *  JAXBElement -> R.AnnotationRef
    *  JAXBElement -> R.Cr
    *  JAXBElement -> R.YearShort
    *  JAXBElement -> FldChar
    *  JAXBElement -> R.Sym
    *  JAXBElement -> R.CommentReference
    *  JAXBElement -> Text
    *  JAXBElement -> R.FootnoteRef
    *  DelText
    *  JAXBElement -> R.DayLong
    *  AlternateContent
    */

    private fun createSubchaptersModel(pPos: Int, level: Int, currentChapter: Subchapter): Int {
        var pos = pPos
        while (pos <= chapter.startPos + chapter.content.size) {
            if (pos == -1) {
                return -1
            }
            if (isHeader(pos, level)) {
                val newChapter = Subchapter(
                    pos,
                    mainDocumentPart.content[pos] as P,
                    currentChapter,
                    Regex("^(?:\\d\\.?){1,3}")
                        .find(TextUtils.getText(mainDocumentPart.content[pos]))!!.value
                        .removeSuffix(".")
                        .split('.')[level - 1]
                        .toInt(),
                    level
                )
                newChapter.content.add(mainDocumentPart.content[pos])
                currentChapter.subchapters.add(newChapter)
                pos = createSubchaptersModel(pos + 1, level + 1, newChapter)
            } else if (isHeader(pos, level - 1)) {
                return pos
            } else if (isHeader(pos, level - 2)) {
                return -1
            } else {
                currentChapter.content.add(mainDocumentPart.content[pos])
                pos++
            }
        }
        return 0
    }

    override fun parse() {
        createSubchaptersModel(chapter.startPos + 1, 2, subchapters)
        validateSubchapters(subchapters.num.toString(), subchapters)
        parseSubchapter(subchapters)
    }

    private fun parseSubchapter(subchapter: Subchapter) {
        if (subchapter.subheader != null) {
            val subheaderPPr = resolver.getEffectivePPr(subchapter.subheader.pPr)
            val isEmpty = TextUtils.getText(subchapter.subheader).isEmpty()
            findPErrors(subchapter.startPos, subheaderPPr, isEmpty, headerPFunctions + commonPFunctions)
            for (r in 0 until subchapter.subheader.content.size) {
                if (subchapter.subheader.content[r] is R) {
                    val rPr = resolver.getEffectiveRPr((subchapter.subheader.content[r] as R).rPr, subchapter.subheader.pPr)
                    findRErrors(subchapter.startPos, r, rPr, isEmpty, headerRFunctions + commonRFunctions)
                } else {
                    handleNotRContent(subchapter.startPos, r)
                }
            }
        }
        for (p in subchapter.startPos + 1 until subchapter.startPos + subchapter.content.size) {
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
                    findRErrors(p, r, rPr, isEmptyP, commonRFunctions + regularRFunctions)
                } else {
                    handleNotRContent(p, r)
                }
            }
        }
        for (sub in subchapter.subchapters) {
            if (subchapter.subchapters.size > 2) {
                parseSubchapter(sub)
            }
        }
    }

    private fun validateSubchapters(expectedNum: String, subchapter: Subchapter) {
        for (sub in 0 until subchapter.subchapters.size) {
            if ("${expectedNum}.${subchapter.subchapters[sub].num}" != "${expectedNum}.${sub + 1}") {
                errors += DocumentError(
                    document.id,
                    this.chapter.startPos,
                    ErrorType.TEXT_BODY_SUBHEADER_NUMBER_ORDER_MISMATCH,
                    "${expectedNum}.${subchapter.subchapters[sub].num}/${expectedNum}.${sub + 1}"
                )
                return
            }
            if (subchapter.level > 3) {
                errors += DocumentError(
                    document.id,
                    this.chapter.startPos,
                    ErrorType.TEXT_BODY_SUBHEADER_LEVEL_WAS_MORE_THAN_3
                )
                return
            }
            validateSubchapters("${expectedNum}.${sub + 1}", subchapter.subchapters[sub])
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
        rFunctionWrappers: Iterable<RFunctionWrapper>
    ) {
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
                BaseCommonRRules::commonRColorCheck,
                BaseCommonRRules::regularRSpacingCheck
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
                BaseRegularRRules::regularRUnderlineCheck
            )
    }

    class Subchapter(val startPos: Int, val subheader: P?, val root: Subchapter?, val num: Int, val level: Int) {
        val subchapters: MutableList<Subchapter> = ArrayList()
        val content: MutableList<Any> = ArrayList()
    }
}