package com.maeasoftworks.polonium.parsers

import com.maeasoftworks.polonium.enums.MistakeType.*
import com.maeasoftworks.polonium.model.Chapter
import com.maeasoftworks.polonium.model.Picture
import com.maeasoftworks.polonium.model.Rules
import com.maeasoftworks.polonium.utils.apply
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R

/**
 * Parser for body chapter.
 *
 * Workflow:
 * 1. Split chapter to subchapters by headers.
 * 2. Recursively validate every subchapter.
 */
class BodyParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    private var isPicturesOrderedInSubchapters: Boolean? = false
    private lateinit var innerPictures: MutableList<Picture>

    /**
     * Root of subchapters model
     */
    private val treeRoot = Subchapter()

    override fun parse() {
        createSubchaptersModel(chapter.startPos + 1, 2, treeRoot)
        validateSubchapters(treeRoot.num.toString(), treeRoot)
        parseHeader()
        parseSubchapter(treeRoot)
        flatPictures()
        root.checkPicturesOrder(
            this,
            if (isPicturesOrderedInSubchapters!!) 1 else 0,
            isPicturesOrderedInSubchapters!!,
            innerPictures
        )
    }

    /**
     * Creates subchapters model by headers recursively. All non-header paragraphs will be added in subchapter content.
     * @param pPos p-layer index
     * @param level nesting level
     * @param currentChapter current subchapter
     * @return end position
     */
    private fun createSubchaptersModel(pPos: Int, level: Int, currentChapter: Subchapter): Int {
        var pos = pPos
        while (pos <= chapter.startPos + chapter.content.size - 1) {
            if (pos == -1) {
                return -1
            }
            if (root.isHeader(pos, level)) {
                val newChapter = Subchapter(
                    pos,
                    root.doc.content[pos] as P,
                    currentChapter,
                    Regex("^(?:\\d\\.?){1,3}")
                        .find(root.texts.getText(root.doc.content[pos] as P))?.value
                        ?.removeSuffix(".")
                        ?.split('.')?.get(level - 1)
                        ?.toInt().also { if (it == null) root.addMistake(TEXT_BODY_SUBHEADER_WAS_EMPTY, pos) },
                    level
                )
                newChapter.content.add(root.doc.content[pos])
                currentChapter.subchapters.add(newChapter)
                currentChapter.subchapters.sortBy { it.level }
                pos = createSubchaptersModel(pos + 1, level + 1, newChapter)
            } else if (root.isHeader(pos, level - 1)) {
                return pos
            } else if (root.isHeader(pos, level - 2)) {
                return -1
            } else {
                currentChapter.content.add(root.doc.content[pos])
                pos++
            }
        }
        return -1
    }

    /**
     * Collect pictures from subchapters recursively
     */
    private fun flatPictures() {
        treeRoot.flatPictures()
        innerPictures = treeRoot.pictures
    }

    private fun parseSubchapter(subchapter: Subchapter) {
        if (subchapter.subheader != null) {
            val subheaderPPr = root.propertiesStorage[subchapter.subheader]
            val isEmpty = root.texts.getText(subchapter.subheader).isEmpty()
            parseAnyHeader(subheaderPPr, subchapter.startPos, subchapter.subheader.content, isEmpty)
        }
        for (p in subchapter.startPos + 1 until subchapter.startPos + subchapter.content.size) {
            if (pictureTitleExpected) {
                pictureTitleExpected = false
                continue
            }
            val pPr = root.propertiesStorage[root.doc.content[p] as P]
            val paragraph = root.doc.content[p] as P
            val isEmptyP = root.texts.getText(paragraph).isBlank()
            commonPFunctions.apply(root, p, pPr, isEmptyP)
            regularPFunctions.apply(root, p, pPr, isEmptyP)
            for (r in 0 until paragraph.content.size) {
                if (paragraph.content[r] is R) {
                    val rPr = root.propertiesStorage[paragraph.content[r] as R]
                    commonRFunctions.apply(root, p, r, rPr, isEmptyP)
                    regularRFunctions.apply(root, p, r, rPr, isEmptyP)
                    for (c in 0 until (paragraph.content[r] as R).content.size) {
                        handleRContent(p, r, c, this, subchapter.pictures)
                    }
                } else {
                    handlePContent(p, r, this)
                }
            }
            if (pPr.numPr != null) {
                validateListElement(p)
            } else {
                listPosition = 0
                currentListStartValue = -1
            }
        }
        for (sub in subchapter.subchapters) {
            parseSubchapter(sub)
        }
    }

    /**
     * Style check for headers
     */
    private fun parseHeader() {
        val headerPPr = root.propertiesStorage[chapter.header]
        val isEmpty = root.texts.getText(chapter.header).isBlank()
        parseAnyHeader(headerPPr, chapter.startPos, chapter.header.content, isEmpty)
    }

    /**
     * Style check for headers and subheaders
     */
    private fun parseAnyHeader(ppr: PPr, startPos: Int, content: List<Any>, isEmpty: Boolean) {
        headerPFunctions.apply(root, startPos, ppr, isEmpty)
        commonPFunctions.apply(root, startPos, ppr, isEmpty)
        for (r in content.indices) {
            if (content[r] is R) {
                val rPr = root.propertiesStorage[content[r] as R]
                headerRFunctions.apply(root, startPos, r, rPr, isEmpty)
                commonRFunctions.apply(root, startPos, r, rPr, isEmpty)
            } else {
                handlePContent(startPos, r, this)
            }
        }
    }

    override fun handleHyperlink(p: Int, r: Int) {
        root.addMistake(TEXT_HYPERLINKS_NOT_ALLOWED_HERE, p, r + 1)
    }

    override fun handleTable(p: Int) {
        //todo add handler
    }

    /**
     * Validates subchapter for style rules
     * @param expectedNum expected header number
     * @param subchapter subchapter to validate
     */
    private fun validateSubchapters(expectedNum: String, subchapter: Subchapter) {
        for (sub in 0 until subchapter.subchapters.size) {
            if ("$expectedNum.${subchapter.subchapters[sub].num}" != "$expectedNum.${sub + 1}") {
                root.addMistake(
                    TEXT_BODY_SUBHEADER_NUMBER_ORDER_MISMATCH,
                    this.chapter.startPos,
                    description = "$expectedNum.${subchapter.subchapters[sub].num}/$expectedNum.${sub + 1}"
                )
                return
            }
            if (subchapter.level > 3) {
                root.addMistake(TEXT_BODY_SUBHEADER_LEVEL_WAS_MORE_THAN_3, this.chapter.startPos)
                return
            }
            validateSubchapters("$expectedNum.${sub + 1}", subchapter.subchapters[sub])
        }
    }

    /**
     * Searches numbers in picture title
     * @param title picture title
     * @return searching result or `null`
     */
    override fun pictureTitleMatcher(title: String): MatchResult? {
        return if (Regex("РИСУНОК (\\d+)").matches(title.uppercase())) {
            isPicturesOrderedInSubchapters = false
            Regex("РИСУНОК (\\d+)").find(title.uppercase())
        } else if (Regex("РИСУНОК (\\d)\\.(\\d+)").matches(title.uppercase())) {
            isPicturesOrderedInSubchapters = true
            Regex("РИСУНОК (\\d)\\.(\\d+)").find(title.uppercase())
        } else null
    }

    companion object {
        private val commonPFunctions = listOf(
            Rules.Default.Common.P.hasNotBackground,
            Rules.Default.Common.P.notBordered
        )

        private val commonRFunctions = listOf(
            Rules.Default.Common.R.isTimesNewRoman,
            Rules.Default.Common.R.fontSizeIs14,
            Rules.Default.Common.R.notItalic,
            Rules.Default.Common.R.notCrossedOut,
            Rules.Default.Common.R.notHighlighted,
            Rules.Default.Common.R.isBlack,
            Rules.Default.Common.R.letterSpacingIs0
        )

        private val headerRFunctions = listOf(
            Rules.Default.Header.R.isBold
        )

        private val headerPFunctions = listOf(
            Rules.Body.Header.P.justifyIsLeft,
            Rules.Body.Header.P.isNotUppercase,
            Rules.Default.Header.P.lineSpacingIsOne,
            Rules.Default.Header.P.emptyLineAfterHeaderExists,
            Rules.Default.Header.P.hasNotDotInEnd,
            Rules.Default.Header.P.firstLineIndentIs1dot25,
            Rules.Default.Header.P.isAutoHyphenSuppressed
        )

        private val regularPFunctions = listOf(
            Rules.Default.RegularText.P.leftIndentIs0,
            Rules.Default.RegularText.P.rightIndentIs0,
            Rules.Default.RegularText.P.firstLineIndentIs1dot25,
            Rules.Default.RegularText.P.justifyIsBoth,
            Rules.Default.RegularText.P.lineSpacingIsOneAndHalf
        )

        private val regularRFunctions = listOf(
            Rules.Default.RegularText.R.isNotBold,
            Rules.Default.RegularText.R.isNotCaps,
            Rules.Default.RegularText.R.isUnderline
        )
    }

    /**
     * @param startPos subchapter's start position in p-layer
     * @param subheader subchapter's header
     * @param parent subchapter's parent
     * @param num subchapter position in parent
     * @param level subchapter nesting level
     */
    inner class Subchapter(
        val startPos: Int,
        val subheader: P?,
        private val parent: Subchapter?,
        val num: Int?,
        val level: Int
    ) {
        val subchapters: MutableList<Subchapter> = ArrayList()
        val content: MutableList<Any> = ArrayList()
        var pictures: MutableList<Picture> = ArrayList()

        constructor() : this(
            chapter.startPos + 1,
            null,
            null,
            //todo: wrong working regex need fix
            Regex("^(?:\\d\\.?){1,3}").find(this@BodyParser.root.texts.getText(chapter.header))?.value?.removeSuffix(".")
                ?.toInt(),
            1
        )

        /**
         * Collects pictures from subchapters recursively
         */
        fun flatPictures() {
            for (subchapter in subchapters) {
                subchapter.flatPictures()
            }
            parent?.pictures?.addAll(pictures)
        }
    }
}
