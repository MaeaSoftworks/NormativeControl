package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.HeadersKeywords
import com.maeasoftworks.normativecontrol.parser.chapters.parsers.*
import com.maeasoftworks.normativecontrol.parser.enums.ChapterType
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType.*
import com.maeasoftworks.normativecontrol.parser.enums.FailureType
import com.maeasoftworks.normativecontrol.parser.enums.State
import org.docx4j.TextUtils
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.P
import org.docx4j.wml.Tbl
import java.io.ByteArrayInputStream

open class DocumentParser(
    open val document: Document,
) {
    lateinit var mlPackage: WordprocessingMLPackage
    lateinit var mainDocumentPart: MainDocumentPart
    lateinit var resolver: PropertyResolver
    var chapters: MutableList<Chapter> = ArrayList()
    var parsers: MutableList<ChapterParser> = ArrayList()
    var errors: MutableList<DocumentError> = ArrayList()
    var tables: MutableList<Tbl> = ArrayList()
    var pictures: MutableList<Any> = ArrayList()

    fun init() {
        try {
            mlPackage = WordprocessingMLPackage.load(ByteArrayInputStream(document.file))
            mainDocumentPart = mlPackage.mainDocumentPart
            resolver = PropertyResolver(mlPackage)
        } catch (e: Docx4JException) {
            document.state = State.ERROR
            document.failureType = FailureType.FILE_READING_ERROR
        }
    }

    fun runVerification(): List<DocumentError> {
        verifyPageSize()
        verifyPageMargins()
        setupChapters()
        createParsers()

        for (parser in parsers) {
            parser.parse(parser)
        }

        return errors
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
            errors += DocumentError(document.id, PAGE_WIDTH_IS_INCORRECT)
        }
        if (pageSize.h.intValueExact() != 16838) {
            errors += DocumentError(document.id, PAGE_HEIGHT_IS_INCORRECT)
        }
    }

    fun verifyPageMargins() {
        val pageMargins = mainDocumentPart.contents.body.sectPr.pgMar
        if (pageMargins.top.intValueExact() != 1134) {
            errors += DocumentError(document.id, PAGE_MARGIN_TOP_IS_INCORRECT)
        }
        if (pageMargins.right.intValueExact() != 850) {
            errors += DocumentError(document.id, PAGE_MARGIN_RIGHT_IS_INCORRECT)
        }
        if (pageMargins.bottom.intValueExact() != 1134) {
            errors += DocumentError(document.id, PAGE_MARGIN_BOTTOM_IS_INCORRECT)
        }
        if (pageMargins.left.intValueExact() != 1701) {
            errors += DocumentError(document.id, PAGE_MARGIN_LEFT_IS_INCORRECT)
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
                    chapters[chapter].type = ChapterType.FRONT_PAGE
                    continue
                }
                val text = TextUtils.getText(chapters[chapter].header)
                if (text.isEmpty()) {
                    emptyChapters += chapter
                    errors.add(DocumentError(document.id, chapter.toLong(), TEXT_HEADER_EMPTY))
                    continue
                }
                chapters[chapter].type = detectNodeType(text, chapter)
            }
        }
        if (!chapters[0].isChapterDetected) {
            chapters[0].type = ChapterType.FRONT_PAGE
        }
        for (empty in emptyChapters) {
            chapters.removeAt(empty)
        }
    }

    private fun detectNodeType(text: String, chapterId: Int): ChapterType {
        if (text.split(Regex("\\s+"))[0].matches(Regex("^(?:\\d{1,2}\\.?){1,3}$"))) {
            return ChapterType.BODY
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
                return ChapterType.APPENDIX
            }
        }

        errors += DocumentError(document.id, chapterId.toLong(), CHAPTER_UNDEFINED_CHAPTER)
        return ChapterType.UNDEFINED
    }

    private fun verifyChapter(
        pos: Int,
        type: ChapterType,
        types: List<ChapterType?>,
        notFound: ErrorType,
        mismatch: ErrorType
    ) {
        try {
            if (type !in types) {
                errors += DocumentError(document.id, pos.toLong(), notFound)
            } else if (chapters[pos].type != type) {
                errors += DocumentError(document.id, pos.toLong(), mismatch)
            }
        } catch (_: IndexOutOfBoundsException) {
        }
    }

    fun verifyChapters() {
        val types = chapters.map { it.type }
        if (chapters.size == 0) {
            errors += DocumentError(document.id, CHAPTER_NO_ONE_CHAPTER_FOUND)
        }
        verifyChapter(
            0,
            ChapterType.FRONT_PAGE,
            types,
            CHAPTER_FRONT_PAGE_NOT_FOUND,
            CHAPTER_FRONT_PAGE_POSITION_MISMATCH
        )
        verifyChapter(
            1,
            ChapterType.ANNOTATION,
            types,
            CHAPTER_ANNOTATION_NOT_FOUND,
            CHAPTER_ANNOTATION_POSITION_MISMATCH
        )
        verifyChapter(
            2,
            ChapterType.CONTENTS,
            types,
            CHAPTER_CONTENTS_NOT_FOUND,
            CHAPTER_CONTENTS_POSITION_MISMATCH
        )
        verifyChapter(
            3,
            ChapterType.INTRODUCTION,
            types,
            CHAPTER_INTRODUCTION_NOT_FOUND,
            CHAPTER_INTRODUCTION_POSITION_MISMATCH
        )
        verifyChapter(
            4,
            ChapterType.BODY,
            types,
            CHAPTER_BODY_NOT_FOUND,
            CHAPTER_BODY_POSITION_MISMATCH
        )
        var i = 4
        try {
            while (chapters[i].type == ChapterType.BODY) {
                i++
            }
        } catch (_: Exception) {
        }
        verifyChapter(
            i,
            ChapterType.CONCLUSION,
            types,
            CHAPTER_CONCLUSION_NOT_FOUND,
            CHAPTER_CONCLUSION_POSITION_MISMATCH
        )
        verifyChapter(
            i + 1,
            ChapterType.REFERENCES,
            types,
            CHAPTER_REFERENCES_NOT_FOUND,
            CHAPTER_REFERENCES_POSITION_MISMATCH
        )
        verifyChapter(
            i + 2,
            ChapterType.APPENDIX,
            types,
            CHAPTER_APPENDIX_NOT_FOUND,
            CHAPTER_APPENDIX_POSITION_MISMATCH
        )
    }

    fun verifyBody() {
        var i = 0
        chapters.filter { it.type == ChapterType.BODY }.forEach {
            if (!TextUtils.getText(it.header).startsWith((++i).toString())) {
                errors += DocumentError(document.id, chapters.indexOf(it).toLong(), CHAPTER_BODY_DISORDER)
            }
        }
    }

    protected fun isHeader(paragraph: Int, level: Int): Boolean {
        val pPr = resolver.getEffectivePPr((mainDocumentPart.content[paragraph] as P).pPr)
        if (pPr == null || pPr.outlineLvl == null) {
            return false
        }
        return pPr.outlineLvl.`val`.toInt() == level - 1
    }

    fun createParsers() {
        for (chapter in chapters) {
            when (chapter.type) {
                ChapterType.FRONT_PAGE -> parsers.add(FrontPageParser(this, chapter))
                ChapterType.ANNOTATION -> parsers.add(SimpleParser(this, chapter))
                ChapterType.CONTENTS -> parsers.add(ContentsParser(this, chapter))
                ChapterType.INTRODUCTION -> parsers.add(SimpleParser(this, chapter))
                ChapterType.BODY -> parsers.add(BodyParser(this, chapter))
                ChapterType.CONCLUSION -> parsers.add(ConclusionParser(this, chapter))
                ChapterType.REFERENCES -> parsers.add(ReferencesParser(this, chapter))
                ChapterType.APPENDIX -> parsers.add(AppendixParser(this, chapter))
                else -> {}
            }
        }
    }
}