package com.maeasoftworks.normativecontrol.dtos

import com.maeasoftworks.normativecontrol.components.CorrectDocumentParams
import com.maeasoftworks.normativecontrol.components.HeadersKeywords
import com.maeasoftworks.normativecontrol.daos.DocumentError
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
import javax.xml.bind.JAXBElement

class DocumentParser(
    val document: Document,
    private var params: CorrectDocumentParams,
    private var keywords: HeadersKeywords,
) {
    private lateinit var mlPackage: WordprocessingMLPackage
    private lateinit var mainDocumentPart: MainDocumentPart
    private lateinit var resolver: PropertyResolver
    val chapters: MutableList<Chapter> = ArrayList()
    val errors: MutableList<DocumentError> = ArrayList()
    val tables: MutableList<Tbl> = ArrayList()
    val pictures: MutableList<Any> = ArrayList()

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

        verifyAnnotation()
        verifyIntroduction()

        return errors
    }

    //region page settings
    fun verifyPageSize() {
        val pageSize = mainDocumentPart.contents.body.sectPr.pgSz
        if (pageSize.w != params.pageWidth) {
            errors += DocumentError(document.id, PAGE_WIDTH_IS_INCORRECT)
        }
        if (pageSize.h != params.pageHeight) {
            errors += DocumentError(document.id, PAGE_HEIGHT_IS_INCORRECT)
        }
    }

    fun verifyPageMargins() {
        val pageMargins = mainDocumentPart.contents.body.sectPr.pgMar
        if (pageMargins.top != params.pageMarginTop) {
            errors += DocumentError(document.id, PAGE_MARGIN_TOP_IS_INCORRECT)
        }
        if (pageMargins.right != params.pageMarginRight) {
            errors += DocumentError(document.id, PAGE_MARGIN_RIGHT_IS_INCORRECT)
        }
        if (pageMargins.bottom != params.pageMarginBottom) {
            errors += DocumentError(document.id, PAGE_MARGIN_BOTTOM_IS_INCORRECT)
        }
        if (pageMargins.left != params.pageMarginLeft) {
            errors += DocumentError(document.id, PAGE_MARGIN_LEFT_IS_INCORRECT)
        }
    }
    //endregion

    fun findChapters() {
        val paragraphs = mainDocumentPart.content
        var paragraph = 0
        var sectorId = 0
        while (paragraph < paragraphs.size) {
            when (paragraphs[paragraph]) {
                is P -> {
                    if (isHeader(paragraph, 1)) {
                        sectorId++
                        for (i in chapters.size..sectorId) {
                            chapters.add(Chapter(paragraph))
                        }
                        chapters[sectorId].header = paragraphs[paragraph] as P
                    }
                }
                is JAXBElement<*> -> {
                    if ((paragraphs[paragraph] as JAXBElement<*>).value is Tbl) {
                        tables.add((paragraphs[paragraph] as JAXBElement<*>).value as Tbl)
                    }
                }
            }
            if (chapters.size <= sectorId) {
                chapters.add(Chapter(paragraph))
            }
            chapters[sectorId].add(paragraphs[paragraph])
            paragraph++
        }
    }

    fun detectChapters() {
        for (node in 0 until chapters.size) {
            if (chapters[node][0] is P) {
                if (chapters[node].header == null) {
                    chapters[node].type = ChapterType.FRONT_PAGE
                    continue
                }
                chapters[node].type = detectNodeType(TextUtils.getText(chapters[node].header), node)
            }
        }
        if (chapters[0].type == null) {
            chapters[0].type = ChapterType.FRONT_PAGE
        }
    }

    private fun detectNodeType(text: String, chapterId: Int): ChapterType {
        if (text.split(Regex("\\s+"))[0].matches(Regex("^(?:\\d{1,2}\\.?){1,3}$"))) {
            return ChapterType.BODY
        }
        for (keys in 0 until keywords.keywordsBySector.size) {
            if (keywords.keywordsBySector[keys].contains(text.uppercase())) {
                return ChapterType.values()[keys]
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
        } catch (_: Exception) {
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

    fun findCommonPRErrors(p: Int) {
        findCommonPRErrors(p, resolver.getEffectivePPr((mainDocumentPart.content[p] as P).pPr))
    }

    private fun findCommonPRErrors(p: Int, pPr: PPr) {
        fun findCommonPErrors(pPr: PPr, p: Int, isEmpty: Boolean) {
            if (pPr.textAlignment != null && pPr.textAlignment.`val` != "left") {
                errors += DocumentError(
                    document.id,
                    p,
                    -1,
                    if (isEmpty) TEXT_WHITESPACE_INCORRECT_DIRECTION else TEXT_COMMON_INCORRECT_DIRECTION
                )
            }
            if (pPr.pBdr != null) {
                errors += DocumentError(document.id, p, -1, if (isEmpty) TEXT_WHITESPACE_BORDER else TEXT_COMMON_BORDER)
            }
            if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
                errors += DocumentError(
                    document.id,
                    p,
                    -1,
                    if (isEmpty) TEXT_WHITESPACE_BACKGROUND_FILL else TEXT_COMMON_BACKGROUND_FILL
                )
            }
        }

        fun findCommonRErrors(rPr: RPr, p: Int, r: Int, isEmpty: Boolean) {
            if (rPr.rFonts.ascii != "Times New Roman") {
                errors += DocumentError(document.id, p, r, if (isEmpty) TEXT_WHITESPACE_FONT else TEXT_COMMON_FONT)
            }
            if (rPr.color != null && rPr.color.`val` != "FFFFFF" && rPr.color.`val` != "auto") {
                errors += DocumentError(
                    document.id,
                    p,
                    r,
                    if (isEmpty) TEXT_WHITESPACE_TEXT_COLOR else TEXT_COMMON_TEXT_COLOR
                )
            }
            if (rPr.sz.`val`.toInt() / 2 != 14) {
                errors += DocumentError(
                    document.id,
                    p,
                    r,
                    if (isEmpty) TEXT_WHITESPACE_INCORRECT_FONT_SIZE else TEXT_COMMON_INCORRECT_FONT_SIZE
                )
            }
            if (!(rPr.i == null || !rPr.i.isVal)) {
                errors += DocumentError(
                    document.id,
                    p,
                    r,
                    if (isEmpty) TEXT_WHITESPACE_ITALIC else TEXT_COMMON_ITALIC_TEXT
                )
            }
            if (!(rPr.strike == null || !rPr.strike.isVal)) {
                errors += DocumentError(
                    document.id,
                    p,
                    r,
                    if (isEmpty) TEXT_WHITESPACE_STRIKETHROUGH else TEXT_COMMON_STRIKETHROUGH
                )
            }
            if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
                errors += DocumentError(
                    document.id,
                    p,
                    r,
                    if (isEmpty) TEXT_WHITESPACE_HIGHLIGHT else TEXT_COMMON_HIGHLIGHT
                )
            }
        }

        val paragraph = mainDocumentPart.content[p] as P
        val text = TextUtils.getText(p)
        val isEmpty = text?.isEmpty() ?: false
        findCommonPErrors(pPr, p, isEmpty)
        for (run in 0 until paragraph.content.size) {
            if (paragraph.content[run] is R) {
                val rPr = resolver.getEffectiveRPr((paragraph.content[run] as R).rPr, paragraph.pPr)
                findCommonRErrors(rPr, p, run, isEmpty)
            }
        }
    }

    fun findHeaderPRErrors(paragraph: Int) {
        val p = mainDocumentPart.content[paragraph] as P
        val pPr = resolver.getEffectivePPr(p.pPr)
        if (mainDocumentPart.content.size <= paragraph + 1) {
            errors += DocumentError(
                document.id,
                paragraph + 1,
                CHAPTER_EMPTY
            )
        } else if (TextUtils.getText(mainDocumentPart.content[paragraph + 1] as P).isNotEmpty()) {
            errors += DocumentError(
                document.id,
                paragraph + 1,
                HEADER_EMPTY_LINE_AFTER_HEADER_REQUIRED
            )
        }
        if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
            errors += DocumentError(document.id, paragraph, 0, TEXT_HEADER_ALIGNMENT)
        }
        val run = if (p.content[0] is JAXBElement<*>) {
            p.content[1] as R
        } else {
            p.content[0] as R
        }
        val rPr = resolver.getEffectiveRPr(run.rPr, pPr)
        val text = TextUtils.getText(run)
        if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
            errors += DocumentError(document.id, paragraph, 0, TEXT_HEADER_NOT_UPPERCASE)
        }
        if (rPr.b == null || !rPr.b.isVal) {
            errors += DocumentError(document.id, paragraph, 0, TEXT_HEADER_NOT_BOLD)
        }
        findCommonPRErrors(paragraph, pPr)
    }

    private fun findRegularTextPRErrors(paragraph: Int) {
        fun findRegularTextPErrors(pPr: PPr, isEmpty: Boolean) {
            if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
                errors += DocumentError(
                    document.id, paragraph,
                    -1,
                    if (isEmpty) TEXT_WHITESPACE_ALIGNMENT else TEXT_REGULAR_INCORRECT_ALIGNMENT
                )
            }
            if (pPr.spacing != null && pPr.spacing.line != null) {
                if (pPr.spacing.lineRule.value() == "auto" && pPr.spacing.line.intValueExact() != 360) {
                    errors += DocumentError(
                        document.id, paragraph,
                        -1,
                        if (isEmpty) TEXT_WHITESPACE_LINE_SPACING else TEXT_COMMON_LINE_SPACING
                    )
                }
            }
            if (pPr.ind != null && pPr.ind.firstLine.intValueExact() != 709) {
                errors += DocumentError(
                    document.id, paragraph,
                    -1,
                    if (isEmpty) TEXT_WHITESPACE_INDENT_FIRST_LINES else TEXT_COMMON_INDENT_FIRST_LINES
                )
            }
            if (pPr.ind != null && pPr.ind.left != null) {
                errors += DocumentError(
                    document.id, paragraph,
                    -1,
                    if (isEmpty) TEXT_WHITESPACE_INDENT_LEFT else TEXT_COMMON_INDENT_LEFT
                )
            }
            if (pPr.ind != null && pPr.ind.right != null) {
                errors += DocumentError(
                    document.id, paragraph,
                    -1,
                    if (isEmpty) TEXT_WHITESPACE_INDENT_RIGHT else TEXT_COMMON_INDENT_RIGHT
                )
            }
        }

        fun findRegularTextRErrors(rPr: RPr, run: Int, isEmpty: Boolean) {
            if (rPr.b != null && !rPr.b.isVal) {
                errors += DocumentError(
                    document.id, paragraph,
                    run, if (isEmpty) TEXT_WHITESPACE_BOLD else TEXT_REGULAR_WAS_BOLD
                )
            }
            if (rPr.caps != null && !rPr.caps.isVal) {
                errors += DocumentError(
                    document.id, paragraph,
                    run, if (isEmpty) TEXT_WHITESPACE_UPPERCASE else TEXT_REGULAR_UPPERCASE
                )
            }
            if (rPr.u != null && rPr.u.`val`.value() != "none") {
                errors += DocumentError(
                    document.id,
                    paragraph,
                    run,
                    if (isEmpty) TEXT_WHITESPACE_UNDERLINED else TEXT_COMMON_UNDERLINED
                )
            }
            if (rPr.spacing != null && rPr.spacing.`val` != null) {
                errors += DocumentError(
                    document.id, paragraph,
                    run, if (isEmpty) TEXT_WHITESPACE_RUN_SPACING else TEXT_COMMON_RUN_SPACING
                )
            }
        }

        val p = mainDocumentPart.content[paragraph] as P
        val pPr = resolver.getEffectivePPr(p.pPr)
        val isEmpty = TextUtils.getText(p).isEmpty()
        findRegularTextPErrors(pPr, isEmpty)

        for (run in 0 until p.content.size) {
            if (p.content[run] is R) {
                val rPr = resolver.getEffectiveRPr((p.content[run] as R).rPr, p.pPr)
                findRegularTextRErrors(rPr, run, isEmpty)
            }
        }

        findCommonPRErrors(paragraph, pPr)
    }

    fun verifyAnnotation() {
        val node = chapters.first { it.type == ChapterType.ANNOTATION }
        val paragraphs = node.content
        findHeaderPRErrors(node.startPos)
        for (paragraph in 1 until paragraphs.size) {
            if (paragraphs[paragraph] !is P) {
                errors += DocumentError(
                    document.id,
                    node.startPos + paragraph,
                    ANNOTATION_MUST_NOT_CONTAINS_MEDIA
                )
            } else {
                findRegularTextPRErrors(node.startPos + paragraph)
            }
        }
    }

    private fun verifyIntroduction() {
        val node = chapters.first { it.type == ChapterType.INTRODUCTION }
        val paragraphs = node.content
        findHeaderPRErrors(node.startPos)
        for (paragraph in 1 until paragraphs.size) {
            findRegularTextPRErrors(node.startPos + paragraph)
        }
    }
}