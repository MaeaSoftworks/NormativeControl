package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.HeadersKeywords
import com.maeasoftworks.docx4nc.enums.ChapterType
import com.maeasoftworks.docx4nc.enums.ChapterType.*
import com.maeasoftworks.docx4nc.enums.MistakeType
import com.maeasoftworks.docx4nc.enums.MistakeType.*
import com.maeasoftworks.docx4nc.model.*
import com.maeasoftworks.normativecontrol.dto.Status
import org.docx4j.TextUtils
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart
import org.docx4j.wml.P
import java.io.ByteArrayInputStream

class DocumentParser(val documentData: DocumentData) {
    private lateinit var mlPackage: WordprocessingMLPackage
    lateinit var mainDocumentPart: MainDocumentPart
    lateinit var resolver: PropertyResolver
    var numbering: NumberingDefinitionsPart? = null

    var chapters: MutableList<Chapter> = ArrayList()
    var parsers: MutableList<ChapterParser> = ArrayList()
    var mistakes: MutableList<MistakeData> = ArrayList()
    var tables: MutableList<Table> = ArrayList()
    val pictures: MutableList<Picture> = ArrayList()
    var comments: CommentsPart? = null

    private var mistakeId: Long = 0

    fun addMistake(type: MistakeType, p: Int? = null, r: Int? = null, description: String? = null) {
        mistakes.add(MistakeData(mistakeId++, p, r, type, description))
    }

    fun addMistake(mistake: MistakeBody?) {
        if (mistake != null) {
            mistakes.add(MistakeData(mistakeId++, mistake.p, mistake.r, mistake.mistakeType))
        }
    }

    fun init() {
        try {
            mlPackage = WordprocessingMLPackage.load(ByteArrayInputStream(documentData.file))
            mainDocumentPart = mlPackage.mainDocumentPart
            mainDocumentPart.contents.body
            resolver = PropertyResolver(mlPackage)
            comments = mainDocumentPart.commentsPart
            numbering = mainDocumentPart.numberingDefinitionsPart
        } catch (e: Docx4JException) {
            documentData.status = Status.ERROR
            documentData.failureType = FailureType.FILE_READING_ERROR
        }
    }

    fun runVerification(): List<MistakeData> {
        verifyPageSize()
        verifyPageMargins()
        setupChapters()
        createParsers()
        for (parser in parsers) {
            parser.parse()
        }
        checkPicturesOrder(AnonymousParser(this), 0, true, pictures)
        return mistakes
    }

    fun setupChapters() {
        findChapters()
        detectChapters()
        verifyChapters()
        verifyBody()
    }

    fun verifyPageSize() {
        val pageSize = mainDocumentPart.contents.body.sectPr.pgSz
        if (pageSize.w.intValueExact() != 11906) {
            addMistake(PAGE_WIDTH_IS_INCORRECT)
        }
        if (pageSize.h.intValueExact() != 16838) {
            addMistake(PAGE_HEIGHT_IS_INCORRECT)
        }
    }

    fun verifyPageMargins() {
        val pageMargins = mainDocumentPart.contents.body.sectPr.pgMar
        if (pageMargins.top.intValueExact() != 1134) {
            addMistake(PAGE_MARGIN_TOP_IS_INCORRECT)
        }
        if (pageMargins.right.intValueExact() != 850) {
            addMistake(PAGE_MARGIN_RIGHT_IS_INCORRECT)
        }
        if (pageMargins.bottom.intValueExact() != 1134) {
            addMistake(PAGE_MARGIN_BOTTOM_IS_INCORRECT)
        }
        if (pageMargins.left.intValueExact() != 1701) {
            addMistake(PAGE_MARGIN_LEFT_IS_INCORRECT)
        }
    }

    fun findChapters() {
        val paragraphs = mainDocumentPart.content
        var paragraph = 0
        var sectorId = 0
        while (paragraph < paragraphs.size) {
            if (paragraphs[paragraph] is P && isHeader(paragraph, 1)) {
                sectorId++
                for (i in chapters.size..sectorId) {
                    chapters.add(Chapter(paragraph))
                }
                chapters[sectorId].header = paragraphs[paragraph] as P
            }
            if (chapters.size <= sectorId) {
                chapters.add(Chapter(paragraph))
            }
            chapters[sectorId].add(paragraphs[paragraph])
            paragraph++
        }
        if (!chapters[0].hasHeader && chapters[0].content.size == 0) {
            chapters.removeAt(0)
        }
    }

    fun detectChapters() {
        val emptyChapters = ArrayList<Int>()
        for (chapter in 0 until chapters.size) {
            if (chapters[chapter][0] is P) {
                if (!chapters[chapter].hasHeader) {
                    chapters[chapter].type = FRONT_PAGE
                    continue
                }
                val text = TextUtils.getText(chapters[chapter].header)
                if (text.isEmpty()) {
                    emptyChapters += chapter
                    addMistake(TEXT_HEADER_EMPTY)
                    continue
                }
                chapters[chapter].type = detectNodeType(text)
            }
        }
        if (!chapters[0].isChapterDetected) {
            chapters[0].type = FRONT_PAGE
        }
        for (empty in emptyChapters) {
            chapters.removeAt(empty)
        }
    }

    private fun detectNodeType(text: String): ChapterType {
        if (text.split(Regex("\\s+"))[0].matches(Regex("^(?:\\d{1,2}\\.?){1,3}$"))) {
            return BODY
        }
        for (keys in 0 until HeadersKeywords.keywordsBySector.size) {
            for (key in HeadersKeywords.keywordsBySector[keys]) {
                if (text.uppercase().contains(key)) {
                    return ChapterType.values()[keys]
                }
            }
        }
        for (keyword in HeadersKeywords.appendix) {
            if (text.uppercase().startsWith(keyword)) {
                return APPENDIX
            }
        }

        addMistake(CHAPTER_UNDEFINED_CHAPTER)
        return UNDEFINED
    }

    private fun verifyChapter(
        pos: Int,
        type: ChapterType,
        types: List<ChapterType?>,
        notFound: MistakeType,
        mismatch: MistakeType
    ) {
        try {
            if (type !in types) {
                addMistake(notFound)
            } else if (chapters[pos].type != type) {
                addMistake(mismatch)
            }
        } catch (_: IndexOutOfBoundsException) {
        }
    }

    fun verifyChapters() {
        val t = chapters.map { it.type }
        if (chapters.size == 0) {
            addMistake(CHAPTER_NO_ONE_CHAPTER_FOUND)
        }
        verifyChapter(0, FRONT_PAGE, t, CHAPTER_FRONT_PAGE_NOT_FOUND, CHAPTER_FRONT_PAGE_POSITION_MISMATCH)
        verifyChapter(1, ANNOTATION, t, CHAPTER_ANNOTATION_NOT_FOUND, CHAPTER_ANNOTATION_POSITION_MISMATCH)
        verifyChapter(2, CONTENTS, t, CHAPTER_CONTENTS_NOT_FOUND, CHAPTER_CONTENTS_POSITION_MISMATCH)
        verifyChapter(3, INTRODUCTION, t, CHAPTER_INTRODUCTION_NOT_FOUND, CHAPTER_INTRODUCTION_POSITION_MISMATCH)
        verifyChapter(4, BODY, t, CHAPTER_BODY_NOT_FOUND, CHAPTER_BODY_POSITION_MISMATCH)
        var i = 4
        try {
            while (chapters[i].type == BODY) {
                i++
            }
        } catch (_: Exception) {
        }
        verifyChapter(i, CONCLUSION, t, CHAPTER_CONCLUSION_NOT_FOUND, CHAPTER_CONCLUSION_POSITION_MISMATCH)
        verifyChapter(i + 1, REFERENCES, t, CHAPTER_REFERENCES_NOT_FOUND, CHAPTER_REFERENCES_POSITION_MISMATCH)
        verifyChapter(i + 2, APPENDIX, t, CHAPTER_APPENDIX_NOT_FOUND, CHAPTER_APPENDIX_POSITION_MISMATCH)
    }

    fun createParsers() {
        for (chapter in chapters) {
            when (chapter.type) {
                FRONT_PAGE -> parsers.add(FrontPageParser(chapter, this))
                ANNOTATION -> parsers.add(SimpleParser(chapter, this))
                CONTENTS -> parsers.add(ContentsParser(chapter, this))
                INTRODUCTION -> parsers.add(SimpleParser(chapter, this))
                BODY -> parsers.add(BodyParser(chapter, this))
                CONCLUSION -> parsers.add(ConclusionParser(chapter, this))
                REFERENCES -> parsers.add(ReferencesParser(chapter, this))
                APPENDIX -> parsers.add(AppendixParser(chapter, this))
                else -> {}
            }
        }
    }

    private fun verifyBody() {
        var i = 0
        chapters.filter { it.type == BODY }.forEach {
            if (!TextUtils.getText(it.header).startsWith((++i).toString())) {
                addMistake(CHAPTER_BODY_DISORDER)
            }
        }
    }

    fun isHeader(paragraph: Int, level: Int? = null): Boolean {
        val pPr = resolver.getEffectivePPr((mainDocumentPart.content[paragraph] as P).pPr)
        if (pPr == null || pPr.outlineLvl == null) {
            return false
        }
        return if (level != null) {
            pPr.outlineLvl.`val`.toInt() == level - 1
        } else {
            pPr.outlineLvl.`val` != null
        }
    }

    fun checkPicturesOrder(
        context: ChapterParser,
        level: Int,
        useInnerIndexer: Boolean,
        container: MutableList<Picture>
    ) {
        if (useInnerIndexer) {
            var index = 1
            for (picture in container) {
                if (picture.title == null) {
                    continue
                }
                val match = context.pictureTitleMatcher(picture.title!!)
                if (match != null) {
                    if (match.groups[1 + level]!!.value.toInt() != index) {
                        addMistake(PICTURE_TITLE_NUMBER_DISORDER, picture.p, picture.r)
                    }
                    context.validatePictureTitleStyle(picture.p)
                } else {
                    addMistake(PICTURE_TITLE_WRONG_FORMAT, picture.p, picture.r)
                }
                index++
            }
        } else {
            context.root.pictures.addAll(container)
        }
    }
}
