package com.maeasoftworks.core.parsers

import com.maeasoftworks.core.enums.ChapterType
import com.maeasoftworks.core.enums.ChapterType.*
import com.maeasoftworks.core.enums.MistakeType
import com.maeasoftworks.core.enums.MistakeType.*
import com.maeasoftworks.core.model.*
import com.maeasoftworks.core.tweaks.PropertiesStorage
import com.maeasoftworks.core.utils.doUntilCatch
import org.docx4j.jaxb.Context
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.exceptions.Docx4JException
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart
import org.docx4j.wml.Comments
import org.docx4j.wml.P
import org.docx4j.wml.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.util.*

/**
 * Main parser class, parses entire document
 * @param byteArrayStream file and file status
 */
class DocumentParser(private val byteArrayStream: ByteArrayInputStream) {
    val texts = Texts(this)

    val mlPackage: WordprocessingMLPackage by lazy {
        try {
            WordprocessingMLPackage.load(byteArrayStream)
        } catch (e: Docx4JException) {
            throw e
        }
    }

    val doc: MainDocumentPart by lazy {
        val result = mlPackage.mainDocumentPart
        result.styleDefinitionsPart.jaxbElement
        result
    }

    val propertiesStorage: PropertiesStorage by lazy { PropertiesStorage(PropertyResolver(mlPackage), this) }

    val autoHyphenation: Boolean? by lazy { doc.documentSettingsPart.jaxbElement.autoHyphenation?.isVal }

    val numbering: NumberingDefinitionsPart? by lazy { doc.numberingDefinitionsPart }

    private val comments: CommentsPart? by lazy {
        (
                doc.commentsPart ?: CommentsPart().also {
                    it.jaxbElement = factory.createComments()
                    doc.addTargetPart(it)
                }
                ).also {
                mistakeId = it.jaxbElement.comment.size.toLong()
            }
    }

    var chapters: MutableList<Chapter> = ArrayList()
    var parsers: MutableList<ChapterParser> = ArrayList()
    var mistakes: MutableList<MistakeOuter> = ArrayList()
    val pictures: MutableList<Picture> = ArrayList()

    private val factory = Context.getWmlObjectFactory()
    private var mistakeId: Long = 0

    fun addMistake(type: MistakeType, p: Int? = null, r: Int? = null, description: String? = null) {
        mistakes.add(MistakeOuter(mistakeId++, p, r, type, description))
    }

    fun addMistake(mistake: MistakeInner?) {
        if (mistake != null) {
            addMistake(mistake.mistakeType, mistake.p, mistake.r, mistake.description)
        }
    }

    fun runVerification() {
        verifyPageSize()
        verifyPageMargins()
        setupChapters()
        createParsers()
        for (parser in parsers) {
            parser.parse()
        }
        checkPicturesOrder(AnonymousParser(this), 0, true, pictures)
    }

    fun writeResult(stream: ByteArrayOutputStream) {
        addComments()
        mlPackage.save(stream)
    }

    private fun addComments() {
        val errors = mistakes.asIterable().sortedWith(compareBy({ it.p }, { it.r })).reversed().toList()
        for (mistake in errors) {
            val comment = createComment(
                mistake.mistakeId,
                mistake.mistakeType.ru.let { x ->
                    if (mistake.description != null) {
                        return@let x + ": ${
                            mistake.description.split('/')
                                .let { if (it.size > 1) "найдено: ${it[0]}, ожидалось: ${it[1]}" else it[0] }
                        }"
                    } else {
                        return@let x
                    }
                }
            )
            comments!!.jaxbElement.comment.add(comment)
            val commentRangeStart = factory.createCommentRangeStart().also {
                it.id = BigInteger.valueOf(mistake.mistakeId)
            }
            val commentRangeEnd = factory.createCommentRangeEnd().also { it.id = BigInteger.valueOf(mistake.mistakeId) }
            if (mistake.p == null) {
                val paragraph = doc.content.first { it is P } as P
                paragraph.content += commentRangeStart
                paragraph.content += commentRangeEnd
                paragraph.content += createRunCommentReference(mistake.mistakeId)
            } else if (mistake.r == null) {
                val paragraph: P = try {
                    doc.content[mistake.p] as P
                } catch (e: ClassCastException) {
                    factory.createP().apply { doc.content.add(mistake.p + 1, this) }
                }
                paragraph.content.add(0, commentRangeStart)
                paragraph.content += commentRangeEnd
                paragraph.content += createRunCommentReference(mistake.mistakeId)
            } else {
                val paragraph: P = try {
                    doc.content[mistake.p] as P
                } catch (e: ClassCastException) {
                    factory.createP().apply { doc.content.add(mistake.p + 1, this) }
                }
                paragraph.content.add(if (mistake.r == 0) 0 else mistake.r - 1, commentRangeStart)
                if (mistake.r == paragraph.content.size - 1) {
                    paragraph.content += commentRangeEnd
                    paragraph.content += createRunCommentReference(mistake.mistakeId)
                } else {
                    paragraph.content.add(mistake.r + 1, commentRangeEnd)
                    paragraph.content.add(mistake.r + 2, createRunCommentReference(mistake.mistakeId))
                }
            }
        }
    }

    fun setupChapters() {
        findChapters()
        detectChapters()
        verifyChapters()
        verifyBody()
    }

    fun verifyPageSize() {
        val pageSize = doc.contents.body.sectPr.pgSz
        if (pageSize.w.intValueExact() != 11906) {
            addMistake(PAGE_WIDTH_IS_INCORRECT)
        }
        if (pageSize.h.intValueExact() != 16838) {
            addMistake(PAGE_HEIGHT_IS_INCORRECT)
        }
    }

    fun verifyPageMargins() {
        val pageMargins = doc.contents.body.sectPr.pgMar
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
        val paragraphs = doc.content
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
        if (chapters[0].hasNotHeader && chapters[0].content.size == 0) {
            chapters.removeAt(0)
        }
    }

    fun detectChapters() {
        val emptyChapters = ArrayList<Int>()
        for (chapter in 0 until chapters.size) {
            if (chapters[chapter][0] is P) {
                if (chapters[chapter].hasNotHeader) {
                    chapters[chapter].type = FRONT_PAGE
                    continue
                }
                val text = texts.getText(chapters[chapter].header)
                if (text.isEmpty()) {
                    emptyChapters += chapter
                    addMistake(TEXT_HEADER_EMPTY)
                    continue
                }
                chapters[chapter].type = detectNodeType(text, chapters[chapter].startPos)
            }
        }
        if (!chapters[0].isChapterDetected) {
            chapters[0].type = FRONT_PAGE
        }
        for (empty in emptyChapters) {
            chapters.removeAt(empty)
        }
    }

    private fun detectNodeType(text: String, startPos: Int): ChapterType {
        if (text.split(Regex("\\s+"))[0].matches(Regex("^(?:\\d{1,2}\\.?){1,3}$"))) {
            return BODY
        }
        for (keys in 0 until ChapterMarkers.markers.size) {
            for (key in ChapterMarkers.markers[keys]) {
                if (text.uppercase().contains(key)) {
                    return ChapterType.entries[keys]
                }
            }
        }
        for (keyword in ChapterMarkers.appendix) {
            if (text.uppercase().startsWith(keyword)) {
                return APPENDIX
            }
        }
        addMistake(CHAPTER_UNDEFINED_CHAPTER, startPos)
        return UNDEFINED
    }

    private fun verifyChapter(
        pos: Int,
        type: ChapterType,
        types: List<ChapterType?>,
        notFound: MistakeType,
        mismatch: MistakeType
    ) = doUntilCatch<IndexOutOfBoundsException> {
        if (type !in types) {
            addMistake(notFound)
        } else if (chapters[pos].type != type) {
            addMistake(mismatch)
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
        doUntilCatch<IndexOutOfBoundsException> {
            while (chapters[i].type == BODY) {
                i++
            }
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
            if (!texts.getText(it.header).startsWith((++i).toString())) {
                addMistake(CHAPTER_BODY_DISORDER)
            }
        }
    }

    fun isHeader(paragraph: Int, level: Int? = null): Boolean {
        val pPr = propertiesStorage[doc.content[paragraph] as P]
        if (pPr.outlineLvl == null) {
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

    private fun createComment(commentId: Long, message: String): Comments.Comment {
        return factory.createCommentsComment().also { comment ->
            comment.id = BigInteger.valueOf(commentId)
            comment.author = "normative control"
            comment.content.add(
                factory.createP().also { p ->
                    p.paraId = "nc-${UUID.randomUUID()}"
                    p.content.add(
                        factory.createR().also { r ->
                            r.content.add(factory.createText().also { text -> text.value = message })
                            r.rPr = factory.createRPr().also {
                                it.rFonts = factory.createRFonts()
                                it.rFonts.ascii = "Consolas"
                                it.rFonts.cs = "Consolas"
                                it.rFonts.eastAsia = "Consolas"
                                it.rFonts.hAnsi = "Consolas"
                            }
                        }
                    )
                }
            )
        }
    }

    private fun createRunCommentReference(commentId: Long): R {
        return factory.createR().also { run ->
            run.content.add(
                factory.createRCommentReference().also {
                    it.id = BigInteger.valueOf(commentId)
                }
            )
        }
    }
}