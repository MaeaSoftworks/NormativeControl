package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.enums.MistakeType.*
import com.maeasoftworks.docx4nc.model.Chapter
import com.maeasoftworks.docx4nc.model.Picture
import com.maeasoftworks.docx4nc.model.Rules
import com.maeasoftworks.docx4nc.utils.apply
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R

/**
 * Класс, ответственный за парсинг страниц, которые были определены как Body (т.е. эти страницы не относятся к типу
 * front, annotation, contents, introduction, conclusion, references, appendix)
 *
 * Как он работает:
 * Парсер разбивает страницу на логические элементы Subchapters, проверяет порядок нумерации заголовков,
 * и если всё нормально - обрабатывает каждую часть по-отдельности
 *
 * @see ChapterMarkers.kt
 *
 * @author prmncr
 */
class BodyParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    private var isPicturesOrderedInSubchapters: Boolean? = false
    private lateinit var innerPictures: MutableList<Picture>

    /**
     * Корневой элемент дерева логических элементов Subchapters
     *
     * @author prmncr
     */
    private val subchapters = Subchapter()

    override fun parse() {
        createSubchaptersModel(chapter.startPos + 1, 2, subchapters)
        validateSubchapters(subchapters.num.toString(), subchapters)
        parseHeader()
        parseSubchapter(subchapters)
        flatPictures()
        root.checkPicturesOrder(
            this,
            if (isPicturesOrderedInSubchapters!!) 1 else 0,
            isPicturesOrderedInSubchapters!!,
            innerPictures
        )
    }

    /**
     * Метод рекурсивно создаёт дерево логических элементов Subchapters
     *
     * Делается проход по параграфам документа.
     * Если параграф содержит в себе заголовок - создаётся Subchapter, она добавляется в список потомков
     * текущей Subchapter (на 1й итерации текущая Subchapter - это root), затем сортирует этот список
     * по уровню вложенности, и метод вызывается рекурсивно
     *
     * После возврата из рекурсивного метода содержимое, не являющиеся заголовком, добавляется
     * в содержимое currentChapter
     *
     * @param pPos порядковый номер параграфа, который содержит подглаву, на странице
     * @param level уровень вложенности текущей подглавы
     * @param currentChapter объект текущей подглавы
     *
     * @author prmncr
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
     * Заполняет список картинок, вложенных в данную подглаву
     *
     * @author prmncr
     */
    private fun flatPictures() {
        subchapters.flatPictures()
        innerPictures = subchapters.pictures
    }

    /**
     * Считывает содержимое страницы в цикле for, при наличии - выделяет Subchapters,
     * затем обрабатывает и их. Во время обработки добавляет в документ ошибки по мере их нахождения
     * применяя к параграфам правила для выявления ошибок
     *
     * @author prmncr
     */
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
     * Считывает заголовки, имеющиеся на странице, и применяет к ним правила для выявления ошибок
     *
     * @author prmncr
     */
    private fun parseHeader() {
        val headerPPr = root.propertiesStorage[chapter.header]
        val isEmpty = root.texts.getText(chapter.header).isBlank()
        parseAnyHeader(headerPPr, chapter.startPos, chapter.header.content, isEmpty)
    }

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

    /**
     * Добавляет ошибку о размещени ссылки в недопустимом месте в основной документ
     *
     * @author prmncr
     */
    override fun handleHyperlink(p: Int, r: Int) {
        root.addMistake(TEXT_HYPERLINKS_NOT_ALLOWED_HERE, p, r + 1)
    }

    override fun handleTable(p: Int) {}

    /**
     * Проходится по списку подглав на данной странице и проверяет их на соответствие правилам
     * (подглавы должны нумероваться в арифметическом порядке,
     * подглавы не должны быть вложены более 3-х раз)
     *
     * @author prmncr
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
     * Метод проверяет, соответствуют ли названия картинок ГОСТ'овскому стандарту
     *
     * @author prmncr
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

    /**
     * Объект, в котором содержатся правила, которым должны соответствовать
     * - параграфы
     * - прогоны
     * - заголовок в прогоне
     * - заголовок в параграфе
     * - текст в параграфе
     * - текст в прогоне
     *
     *
     * @author prmncr
     */
    companion object {
        /**
         * Правила, которым должны соответствовать параграфы
         *
         * @author prmncr
         */
        private val commonPFunctions = listOf(
            Rules.Default.Common.P::hasNotBackground,
            Rules.Default.Common.P::notBordered
        )

        /**
         * Правила, которым должны соответствовать прогоны
         *
         * @author prmncr
         */
        private val commonRFunctions = listOf(
            Rules.Default.Common.R::isTimesNewRoman,
            Rules.Default.Common.R::fontSizeIs14,
            Rules.Default.Common.R::notItalic,
            Rules.Default.Common.R::notCrossedOut,
            Rules.Default.Common.R::notHighlighted,
            Rules.Default.Common.R::isBlack,
            Rules.Default.Common.R::letterSpacingIs0
        )

        /**
         * Правила, которым должны соответствовать заголовки в прогоне
         *
         * @author prmncr
         */
        private val headerRFunctions = listOf(
            Rules.Default.Header.R::isBold
        )

        /**
         * Правила, которым должны соответствовать заголовки в параграфе
         *
         * @author prmncr
         */
        private val headerPFunctions = listOf(
            Rules.Body.Header.P::justifyIsLeft,
            Rules.Body.Header.P::isNotUppercase,
            Rules.Default.Header.P::lineSpacingIsOne,
            Rules.Default.Header.P::emptyLineAfterHeaderExists,
            Rules.Default.Header.P::hasNotDotInEnd,
            Rules.Default.Header.P::firstLineIndentIs1dot25,
            Rules.Default.Header.P::isAutoHyphenSuppressed
        )

        /**
         * Правила, которым должен соответствовать текст в параграфе
         *
         * @author prmncr
         */
        private val regularPFunctions = listOf(
            Rules.Default.RegularText.P::leftIndentIs0,
            Rules.Default.RegularText.P::rightIndentIs0,
            Rules.Default.RegularText.P::firstLineIndentIs1dot25,
            Rules.Default.RegularText.P::justifyIsBoth,
            Rules.Default.RegularText.P::lineSpacingIsOneAndHalf
        )

        /**
         * Правила, которым должен соответствовать текст в прогоне
         *
         * @author prmncr
         */
        private val regularRFunctions = listOf(
            Rules.Default.RegularText.R::isNotBold,
            Rules.Default.RegularText.R::isNotCaps,
            Rules.Default.RegularText.R::isUnderline
        )
    }

    /**
     * Класс, инкапсулирующий содержимое логического элемента внутри страницы
     *
     * В одной Subchapter должно быть не более трёх Subchapters
     *
     * @author prmncr
     */
    inner class Subchapter(

        /**
         * Номер параграфа на странице, в котором начинается Subchapter
         *
         * @author prmncr
         */
        val startPos: Int,

        /**
         * Первый подзаголовок в Subchapter
         *
         * @author prmncr
         */
        val subheader: P?,

        /**
         * Родительский объект Subchapter для данного Subchapter
         *
         * @author prmncr
         */
        private val root: Subchapter?,

        /**
         * Порядковый номер этой Subchapter
         *
         * @author prmncr
         */
        val num: Int?,

        /**
         * Уровень вложенности этой Subchapter
         *
         * @author prmncr
         */
        val level: Int
    ) {

        constructor() : this(
            chapter.startPos + 1,
            null,
            null,
            Regex("^(?:\\d\\.?){1,3}").find(this@BodyParser.root.texts.getText(chapter.header))?.value?.removeSuffix(".")
                ?.toInt(),
            1
        )

        /**
         * Метод для получения всех картинок в Subchapter и её Subchapters
         *
         * Метод проходит циклом foreach по каждой Subchapter из поля subchapters, рекурсивно вызывая в каждой Subchapter
         * самого себя, после чего добавляет в поле pictures своего корневого элемента содержимое своего поля pictures
         *
         * @author prmncr
         */
        fun flatPictures() {
            for (subchapter in subchapters) {
                subchapter.flatPictures()
            }
            root?.pictures?.addAll(pictures)
        }

        /**
         * Список Subchapters этой Subchapter
         *
         * @author prmncr
         */
        val subchapters: MutableList<Subchapter> = ArrayList()

        /**
         * Текстовое содержимое этой Subchapter
         *
         * @author prmncr
         */
        val content: MutableList<Any> = ArrayList()

        /**
         * Картинки этой Subchapter
         *
         * @author prmncr
         */
        var pictures: MutableList<Picture> = ArrayList()
    }
}
