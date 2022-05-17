package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.Rules
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.docx4j.TextUtils
import org.docx4j.wml.*

class BodyParser(parser: DocumentParser, chapter: Chapter) : ChapterParser(parser, chapter) {
    private val subchapters = Subchapter(
        chapter.startPos + 1,
        null,
        Regex("^(?:\\d\\.?){1,3}").let {
            val text = TextUtils.getText(chapter.header)
            return@let if (text != null) {
                it.find(text)?.value?.removeSuffix(".")?.toInt()
            } else {
                null
            }
        },
        1
    )

    private fun createSubchaptersModel(pPos: Int, level: Int, currentChapter: Subchapter): Int {
        var pos = pPos
        while (pos <= chapter.startPos + chapter.content.size - 1) {
            if (pos == -1) {
                return -1
            }
            if (isHeader(pos, level)) {
                val newChapter = Subchapter(
                    pos,
                    mainDocumentPart.content[pos] as P,
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

    override fun parse(parser: ChapterParser) {
        createSubchaptersModel(chapter.startPos + 1, 2, subchapters)
        validateSubchapters(subchapters.num.toString(), subchapters)
        parseHeader()
        parseSubchapter(subchapters)
    }

    private fun parseSubchapter(subchapter: Subchapter) {
        if (subchapter.subheader != null) {
            val subheaderPPr = resolver.getEffectivePPr(subchapter.subheader.pPr)
            val isEmpty = TextUtils.getText(subchapter.subheader).isEmpty()
            applyPFunctions(subchapter.startPos, subheaderPPr, isEmpty, headerPFunctions + commonPFunctions)
            for (r in 0 until subchapter.subheader.content.size) {
                if (subchapter.subheader.content[r] is R) {
                    val rPr = resolver.getEffectiveRPr((subchapter.subheader.content[r] as R).rPr, subchapter.subheader.pPr)
                    applyRFunctions(subchapter.startPos, r, rPr, isEmpty, headerRFunctions + commonRFunctions)
                } else {
                    handlePContent(subchapter.startPos, r, this)
                }
            }
        }
        for (p in subchapter.startPos + 1 until subchapter.startPos + subchapter.content.size) {
            val pPr = resolver.getEffectivePPr((mainDocumentPart.content[p] as P).pPr)
            val paragraph = mainDocumentPart.content[p] as P
            val isEmptyP = TextUtils.getText(paragraph).isEmpty()
            applyPFunctions(p, pPr, isEmptyP, commonPFunctions + regularPBeforeListCheckFunctions)
            if (pPr.numPr != null) {
                validateList(p)
                continue
            }
            applyPFunctions(p, pPr, isEmptyP, regularPAfterListCheckFunctions)
            for (r in 0 until paragraph.content.size) {
                if (paragraph.content[r] is R) {
                    val rPr = resolver.getEffectiveRPr((paragraph.content[r] as R).rPr, paragraph.pPr)
                    applyRFunctions(p, r, rPr, isEmptyP, commonRFunctions + regularRFunctions)
                } else {
                    handlePContent(p, r, this)
                }
            }
        }
        for (sub in subchapter.subchapters) {
            parseSubchapter(sub)
        }
    }

    override fun parseHeader() {
        val headerPPr = resolver.getEffectivePPr(chapter.header.pPr)
        val isEmpty = TextUtils.getText(chapter.header).isEmpty()
        applyPFunctions(chapter.startPos, headerPPr, isEmpty, headerPFunctions + commonPFunctions)
        for (r in 0 until chapter.header.content.size) {
            if (chapter.header.content[r] is R) {
                val rPr = resolver.getEffectiveRPr((chapter.header.content[r] as R).rPr, chapter.header.pPr)
                applyRFunctions(chapter.startPos, r, rPr, isEmpty, headerRFunctions + commonRFunctions)
            } else {
                handlePContent(chapter.startPos, r, this)
            }
        }
    }

    override fun parseP(p: Int, pPr: PPr, isEmpty: Boolean) {}
    override fun parseR(p: Int, r: Int, paragraph: P) {}


    override fun handleHyperlink(p: Int, r: Int) {
        errors += DocumentError(document.id, p, r, ErrorType.TEXT_HYPERLINKS_NOT_ALLOWED_HERE)
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

    override fun applyPFunctions(p: Int, pPr: PPr, isEmpty: Boolean, pFunctionWrappers: Iterable<PFunctionWrapper>) {
        for (wrapper in pFunctionWrappers) {
            val error = wrapper.function(document.id, p, pPr, isEmpty, mainDocumentPart)
            if (error != null) {
                errors += error
            }
        }
    }

    override fun applyRFunctions(
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

    companion object {
        private val commonPFunctions =
            createPRulesCollection(
                Rules.Default.Common.P::textAlignIsBoth,
                Rules.Default.Common.P::hasNotBackground,
                Rules.Default.Common.P::notBordered,
                Rules.Default.Common.P::textAlignIsBoth
            )

        private val commonRFunctions =
            createRRulesCollection(
                Rules.Default.Common.R::isTimesNewRoman,
                Rules.Default.Common.R::fontSizeIs14,
                Rules.Default.Common.R::notItalic,
                Rules.Default.Common.R::notCrossedOut,
                Rules.Default.Common.R::notHighlighted,
                Rules.Default.Common.R::isBlack,
                Rules.Default.Common.R::letterSpacingIs0
            )

        private val headerRFunctions =
            createRRulesCollection(
                Rules.Default.Header.R::isBold
            )

        private val headerPFunctions =
            createPRulesCollection(
                Rules.Body.Header.P::justifyIsLeft,
                Rules.Body.Header.P::isNotUppercase,
                Rules.Default.Header.P::lineSpacingIsOneAndHalf,
                Rules.Default.Header.P::emptyLineAfterHeaderExists,
                Rules.Default.Header.P::hasNotDotInEnd,
                Rules.Default.RegularText.P::firstLineIndentIs1dot25,
            )

        private val regularPAfterListCheckFunctions =
            createPRulesCollection(
                Rules.Default.RegularText.P::leftIndentIs0,
                Rules.Default.RegularText.P::rightIndentIs0,
                Rules.Default.RegularText.P::firstLineIndentIs1dot25
            )

        private val regularPBeforeListCheckFunctions =
            createPRulesCollection(
                Rules.Default.RegularText.P::justifyIsBoth,
                Rules.Default.RegularText.P::lineSpacingIsOneAndHalf
            )

        private val regularRFunctions =
            createRRulesCollection(
                Rules.Default.RegularText.R::isNotBold,
                Rules.Default.RegularText.R::isNotCaps,
                Rules.Default.RegularText.R::isUnderline
            )
    }

    class Subchapter(val startPos: Int, val subheader: P?, val num: Int?, val level: Int) {
        val subchapters: MutableList<Subchapter> = ArrayList()
        val content: MutableList<Any> = ArrayList()
    }
}