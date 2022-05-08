package com.maeasoftworks.normativecontrol.dtos

import com.maeasoftworks.normativecontrol.components.CorrectDocumentParams
import com.maeasoftworks.normativecontrol.components.HeadersKeywords
import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType
import com.maeasoftworks.normativecontrol.dtos.enums.FailureType
import com.maeasoftworks.normativecontrol.dtos.enums.ChapterType
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
        findNodes()
        detectNodes()
        verifyNodes()

        verifyAnnotation()
        verifyIntroduction()

        return errors
    }

    //region page settings
    fun verifyPageSize() {
        val pageSize = mainDocumentPart.contents.body.sectPr.pgSz
        if (pageSize.w != params.pageWidth) {
            errors.add(DocumentError(document.id, ErrorType.PAGE_WIDTH_IS_INCORRECT))
        }
        if (pageSize.h != params.pageHeight) {
            errors.add(DocumentError(document.id, ErrorType.PAGE_HEIGHT_IS_INCORRECT))
        }
    }

    fun verifyPageMargins() {
        val pageMargins = mainDocumentPart.contents.body.sectPr.pgMar
        if (pageMargins.top != params.pageMarginTop) {
            errors.add(DocumentError(document.id, ErrorType.PAGE_MARGIN_TOP_IS_INCORRECT))
        }
        if (pageMargins.right != params.pageMarginRight) {
            errors.add(DocumentError(document.id, ErrorType.PAGE_MARGIN_RIGHT_IS_INCORRECT))
        }
        if (pageMargins.bottom != params.pageMarginBottom) {
            errors.add(DocumentError(document.id, ErrorType.PAGE_MARGIN_BOTTOM_IS_INCORRECT))
        }
        if (pageMargins.left != params.pageMarginLeft) {
            errors.add(DocumentError(document.id, ErrorType.PAGE_MARGIN_LEFT_IS_INCORRECT))
        }
    }
    //endregion

    fun findNodes() {
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

    fun detectNodes() {
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
        errors.add(DocumentError(document.id, chapterId.toLong(), ErrorType.CHAPTER_UNDEFINED_CHAPTER))
        return ChapterType.UNDEFINED
    }

    private fun verifyNode(pos: Int, type: ChapterType, types: List<ChapterType?>, notFound: ErrorType, mismatch: ErrorType) {
        try {
            if (type !in types) {
                errors.add(DocumentError(document.id, pos.toLong(), notFound))
            } else if (chapters[pos].type != type) {
                errors.add(DocumentError(document.id, pos.toLong(), mismatch))
            }
        } catch (_: Exception) {}
    }

    fun verifyNodes() {
        val types = chapters.map { it.type }
        if (chapters.size == 0) {
            errors.add(DocumentError(document.id, ErrorType.CHAPTER_NO_ONE_CHAPTER_FOUND))
        }
        verifyNode(0, ChapterType.FRONT_PAGE, types, ErrorType.CHAPTER_FRONT_PAGE_NOT_FOUND, ErrorType.CHAPTER_FRONT_PAGE_POSITION_MISMATCH)
        verifyNode(1, ChapterType.ANNOTATION, types, ErrorType.CHAPTER_ANNOTATION_NOT_FOUND, ErrorType.CHAPTER_ANNOTATION_POSITION_MISMATCH)
        verifyNode(2, ChapterType.CONTENTS, types, ErrorType.CHAPTER_CONTENTS_NOT_FOUND, ErrorType.CHAPTER_CONTENTS_POSITION_MISMATCH)
        verifyNode(3, ChapterType.INTRODUCTION, types, ErrorType.CHAPTER_INTRODUCTION_NOT_FOUND, ErrorType.CHAPTER_INTRODUCTION_POSITION_MISMATCH)
        verifyNode(4, ChapterType.BODY, types, ErrorType.CHAPTER_BODY_NOT_FOUND, ErrorType.CHAPTER_BODY_POSITION_MISMATCH)
        var i = 4
        try {
            while (chapters[i].type == ChapterType.BODY) {
                i++
            }
        } catch (_: Exception) {}
        verifyNode(i, ChapterType.CONCLUSION, types, ErrorType.CHAPTER_CONCLUSION_NOT_FOUND, ErrorType.CHAPTER_CONCLUSION_POSITION_MISMATCH)
        verifyNode(i + 1, ChapterType.REFERENCES, types, ErrorType.CHAPTER_REFERENCES_NOT_FOUND, ErrorType.CHAPTER_REFERENCES_POSITION_MISMATCH)
        verifyNode(i + 2, ChapterType.APPENDIX, types, ErrorType.CHAPTER_APPENDIX_NOT_FOUND, ErrorType.CHAPTER_APPENDIX_POSITION_MISMATCH)
    }

    private fun isHeader(paragraph: Int, level: Int): Boolean {
        val pPr = resolver.getEffectivePPr((mainDocumentPart.content[paragraph] as P).pPr)
        if (pPr == null || pPr.outlineLvl == null) {
            return false
        }
        return pPr.outlineLvl.`val`.toInt() == level - 1
    }

    fun findGeneralAllErrors(p: Int) {
        findGeneralAllErrors(p, resolver.getEffectivePPr((mainDocumentPart.content[p] as P).pPr))
    }

    private fun findGeneralAllErrors(p: Int, pPr: PPr) {
        fun findGeneralPErrors(pPr: PPr, p: Int) {
            if (pPr.textAlignment != null && pPr.textAlignment.`val` != "left") {
                errors.add(DocumentError(document.id, p, -1, ErrorType.TEXT_COMMON_INCORRECT_DIRECTION))
            }
            if (pPr.pBdr != null) {
                errors.add(DocumentError(document.id, p, -1, ErrorType.TEXT_COMMON_BORDER))
            }
            if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
                errors.add(DocumentError(document.id, p, -1, ErrorType.TEXT_COMMON_BACKGROUND_FILL))
            }
        }

        fun findCommonRErrors(rPr: RPr, p: Int, r: Int) {
            if (rPr.rFonts.ascii != "Times New Roman") {
                errors.add(DocumentError(document.id, p, r, ErrorType.TEXT_COMMON_FONT))
            }
            if (rPr.color != null && rPr.color.`val` != "FFFFFF" && rPr.color.`val` != "auto") {
                errors.add(DocumentError(document.id, p, r, ErrorType.TEXT_COMMON_INCORRECT_COLOR))
            }
            if (rPr.sz.`val`.toInt() / 2 != 14) {
                errors.add(DocumentError(document.id, p, r, ErrorType.TEXT_COMMON_INCORRECT_FONT_SIZE))
            }
            if (!(rPr.i == null || !rPr.i.isVal)) {
                errors.add(DocumentError(document.id, p, r, ErrorType.TEXT_COMMON_ITALIC_TEXT))
            }
            if (!(rPr.strike == null || !rPr.strike.isVal)) {
                errors.add(DocumentError(document.id, p, r, ErrorType.TEXT_COMMON_STRIKETHROUGH))
            }
            if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
                errors.add(DocumentError(document.id, p, r, ErrorType.TEXT_COMMON_HIGHLIGHT))
            }
        }

        val paragraph = mainDocumentPart.content[p] as P
        findGeneralPErrors(pPr, p)
        for (run in 0 until paragraph.content.size) {
            val rPr = resolver.getEffectiveRPr((paragraph.content[run] as R).rPr, paragraph.pPr)
            findCommonRErrors(rPr, p, run)
        }
    }

    fun findHeaderAllErrors(paragraph: Int) {
        val p = mainDocumentPart.content[paragraph] as P
        val pPr = resolver.getEffectivePPr(p.pPr)
        if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
            errors.add(DocumentError(document.id, paragraph, 0, ErrorType.TEXT_HEADER_ALIGNMENT))
        }
        val run = p.content[0] as R
        val rPr = resolver.getEffectiveRPr(run.rPr, pPr)
        val text = TextUtils.getText(run)
        if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
            errors.add(DocumentError(document.id, paragraph, 0, ErrorType.TEXT_HEADER_NOT_UPPERCASE))
        }
        findGeneralAllErrors(paragraph, pPr)
    }

    private fun findRegularTextAllErrors(paragraph: Int) {
        fun findRegularTextPErrors(pPr: PPr, isEmpty: Boolean) {
            if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
                errors.add(
                    DocumentError(
                        document.id, paragraph,
                        -1, if (!isEmpty) ErrorType.TEXT_REGULAR_INCORRECT_ALIGNMENT
                        else ErrorType.TEXT_WHITESPACE_ALIGNMENT
                    )
                )
            }
        }

        fun findRegularTextRErrors(rPr: RPr, run: Int, isEmpty: Boolean) {
            if (rPr.b != null && !rPr.b.isVal) {
                errors.add(
                    DocumentError(
                        document.id, paragraph,
                        run, if (!isEmpty) ErrorType.TEXT_REGULAR_WAS_BOLD else ErrorType.TEXT_WHITESPACE_BOLD
                    )
                )
            }
            if (rPr.u != null && rPr.u.`val`.value() != "none") {
                errors.add(
                    DocumentError(
                        document.id,
                        paragraph,
                        run,
                        if (!isEmpty) ErrorType.TEXT_COMMON_UNDERLINED else ErrorType.TEXT_WHITESPACE_UNDERLINED
                    )
                )
            }
        }

        val p = mainDocumentPart.content[paragraph] as P
        val pPr = resolver.getEffectivePPr(p.pPr)
        val isEmpty = TextUtils.getText(p).isEmpty()
        findRegularTextPErrors(pPr, isEmpty)

        for (run in 0 until p.content.size) {
            val rPr = resolver.getEffectiveRPr((p.content[run] as R).rPr, p.pPr)
            findRegularTextRErrors(rPr, run, isEmpty)
        }

        findGeneralAllErrors(paragraph, pPr)
    }

    fun verifyAnnotation() {
        val node = chapters.first { it.type == ChapterType.ANNOTATION }
        val paragraphs = node.content
        for (paragraph in 1 until paragraphs.size) {
            if (paragraphs[paragraph] !is P) {
                errors.add(
                    DocumentError(
                        document.id,
                        node.startPos + paragraph,
                        ErrorType.ANNOTATION_MUST_NOT_CONTAINS_MEDIA
                    )
                )
            } else {
                findRegularTextAllErrors(node.startPos + paragraph)
            }
        }
    }

    private fun verifyIntroduction() {
        val node = chapters.first { it.type == ChapterType.INTRODUCTION }
        val paragraphs = node.content
        for (paragraph in 1 until paragraphs.size) {
            findRegularTextAllErrors(node.startPos + paragraph)
        }
    }
}