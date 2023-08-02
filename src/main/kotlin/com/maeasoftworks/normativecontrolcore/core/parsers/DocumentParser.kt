package com.maeasoftworks.normativecontrolcore.core.parsers

import com.maeasoftworks.normativecontrolcore.core.context.Context
import com.maeasoftworks.normativecontrolcore.core.model.DocumentChildParsers
import com.maeasoftworks.normativecontrolcore.core.model.Picture
import com.maeasoftworks.normativecontrolcore.core.tweaks.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Main parser class, parses entire document
 * @param byteArrayStream file and file status
 */
class DocumentParser(byteArrayStream: ByteArrayInputStream) {
    private val mlPackage: WordprocessingMLPackage = WordprocessingMLPackage.load(byteArrayStream)
    val resolver: PropertyResolver = PropertyResolver(mlPackage)
    private val ctx = Context(mlPackage, resolver)
    val doc: MainDocumentPart by lazy {
        val result = mlPackage.mainDocumentPart
        result.styleDefinitionsPart.jaxbElement
        result
    }
    val autoHyphenation: Boolean? by lazy { doc.documentSettingsPart.jaxbElement.autoHyphenation?.isVal }
    val numbering: NumberingDefinitionsPart? by lazy { doc.numberingDefinitionsPart }
    val pictures: MutableList<Picture> = ArrayList()

    fun runVerification() {
        while (ctx.ptr.bodyPosition < ctx.ptr.totalChildSize) {
            val currentChild = doc.content[ctx.ptr.bodyPosition]
            DocumentChildParsers.parseDocumentChild(currentChild, ctx)
            ctx.ptr.moveNextChild()
        }

        //verifyPageSize()
        //verifyPageMargins()
        //setupChapters()
        //createParsers()
        //for (parser in parsers) {
        //    parser.parse()
        //}
        //checkPicturesOrder(AnonymousParser(this), 0, true, pictures)
    }

    fun writeResult(stream: ByteArrayOutputStream) {
        mlPackage.save(stream)
    }

    /*

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

    private fun verifyPageMargins() {
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
            if (paragraphs[paragraph] is P && isHeaderOfLevel(paragraph, 1)) {
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

    fun isHeaderOfLevel(paragraph: Int, level: Int? = null): Boolean {
        val lvl = resolver.getActualProperty((doc.content[paragraph] as P)) { outlineLvl } ?: return false
        return if (level != null) {
            lvl.`val`.toInt() == level - 1
        } else {
            lvl.`val` != null
        }
    }

    fun checkPicturesOrder(
        context: ChapterParserClass,
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

    */
}
