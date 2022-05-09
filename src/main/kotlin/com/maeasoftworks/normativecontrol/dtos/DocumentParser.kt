package com.maeasoftworks.normativecontrol.dtos

import com.maeasoftworks.normativecontrol.components.HeadersKeywords
import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.dtos.chapters.*
import com.maeasoftworks.normativecontrol.dtos.enums.ChapterType
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType.*
import com.maeasoftworks.normativecontrol.dtos.enums.FailureType
import com.maeasoftworks.normativecontrol.dtos.enums.State
import org.docx4j.TextUtils
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import java.io.ByteArrayInputStream

open class DocumentParser(
    open val document: Document,
    open val keywords: HeadersKeywords,
) {
    open lateinit var mlPackage: WordprocessingMLPackage
    open lateinit var mainDocumentPart: MainDocumentPart
    open lateinit var resolver: PropertyResolver
    open var chapters: MutableList<Chapter> = ArrayList()
    open var parsers: MutableList<ChapterParser?> = ArrayList()
    open var errors: MutableList<DocumentError> = ArrayList()
    open var tables: MutableList<Tbl> = ArrayList()
    open var pictures: MutableList<Any> = ArrayList()

    fun init() {
        try {
            mlPackage = WordprocessingMLPackage.load(ByteArrayInputStream(document.file))
            mainDocumentPart = mlPackage.mainDocumentPart
            resolver = PropertyResolver(mlPackage)
        } catch (e: Docx4JException) {
            document.state = State.ERROR
            document.failureType = FailureType.FILE_READING_ERROR
            return
        }
    }

    fun runVerification(): List<DocumentError> {
        verifyPageSize()
        verifyPageMargins()
        findChapters()
        detectChapters()
        verifyChapters()
        createParsers()

        for (parser in parsers) {
            parser?.parse()
        }

        return errors
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
    }

    fun detectChapters() {
        val emptyChapters = ArrayList<Int>()
        for (chapter in 0 until chapters.size) {
            if (chapters[chapter][0] is P) {
                if (chapters[chapter].header == null) {
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
        if (chapters[0].type == null) {
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
        for (keys in 0 until keywords.keywordsBySector.size) {
            for (key in keywords.keywordsBySector[keys]) {
                if (text.uppercase().contains(key)) {
                    return ChapterType.values()[keys]
                }
            }
        }
        for (keyword in keywords.appendix) {
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

    private fun isHeader(paragraph: Int, level: Int): Boolean {
        val pPr = resolver.getEffectivePPr((mainDocumentPart.content[paragraph] as P).pPr)
        if (pPr == null || pPr.outlineLvl == null) {
            return false
        }
        return pPr.outlineLvl.`val`.toInt() == level - 1
    }

    fun createParsers() {
        for (chapter in chapters) {
            parsers.add(
                when (chapter.type) {
                    ChapterType.FRONT_PAGE -> FrontPageParser(this, chapter)
                    ChapterType.ANNOTATION -> SimpleParser(this, chapter)
                    ChapterType.CONTENTS -> ContentsParser(this, chapter)
                    ChapterType.INTRODUCTION -> SimpleParser(this, chapter)
                    ChapterType.BODY -> BodyParser(this, chapter)
                    ChapterType.CONCLUSION -> ConclusionParser(this, chapter)
                    ChapterType.REFERENCES -> ReferencesParser(this, chapter)
                    ChapterType.APPENDIX -> AppendixParser(this, chapter)
                    else -> null
                }
            )
        }
    }
}