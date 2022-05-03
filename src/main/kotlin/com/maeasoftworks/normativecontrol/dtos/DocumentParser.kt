package com.maeasoftworks.normativecontrol.dtos

import com.maeasoftworks.normativecontrol.components.CorrectDocumentParams
import com.maeasoftworks.normativecontrol.components.HeadersKeywords
import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.prmncr.normativecontrol.dtos.Node
import com.prmncr.normativecontrol.dtos.NodeType
import org.docx4j.TextUtils
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import javax.xml.bind.JAXBElement

class DocumentParser(
    mlPackage: WordprocessingMLPackage,
    private var params: CorrectDocumentParams,
    private var keywords: HeadersKeywords,
    private val documentId: String?
) {
    private val document: MainDocumentPart = mlPackage.mainDocumentPart
    private val resolver: PropertyResolver = PropertyResolver(mlPackage)
    val nodes: MutableList<Node> = ArrayList()
    val errors: MutableList<DocumentError> = ArrayList()
    val tables: MutableList<Tbl> = ArrayList()
    val pictures: MutableList<Any> = ArrayList()

    fun runStyleCheck(): List<DocumentError> {
        checkPageSize()
        checkPageMargins()
        findSectors()
        detectNodes()
        findIncorrectNodes()
        return errors
    }

    //region page settings
    fun checkPageSize() {
        val pageSize = document.contents.body.sectPr.pgSz
        if (pageSize.w != params.pageWidth) {
            errors.add(DocumentError(ErrorType.PAGE_WIDTH_IS_INCORRECT, documentId))
        }
        if (pageSize.h != params.pageHeight) {
            errors.add(DocumentError(ErrorType.PAGE_HEIGHT_IS_INCORRECT, documentId))
        }
    }

    fun checkPageMargins() {
        val pageMargins = document.contents.body.sectPr.pgMar
        if (pageMargins.top != params.pageMarginTop) {
            errors.add(DocumentError(ErrorType.PAGE_MARGIN_TOP_IS_INCORRECT, documentId))
        }
        if (pageMargins.right != params.pageMarginRight) {
            errors.add(DocumentError(ErrorType.PAGE_MARGIN_RIGHT_IS_INCORRECT, documentId))
        }
        if (pageMargins.bottom != params.pageMarginBottom) {
            errors.add(DocumentError(ErrorType.PAGE_MARGIN_BOTTOM_IS_INCORRECT, documentId))
        }
        if (pageMargins.left != params.pageMarginLeft) {
            errors.add(DocumentError(ErrorType.PAGE_MARGIN_LEFT_IS_INCORRECT, documentId))
        }
    }
    //endregion

    fun findSectors() {
        val paragraphs = document.content
        var paragraph = 0
        var sectorId = 0
        while (paragraph < paragraphs.size) {
            when (paragraphs[paragraph]) {
                is P -> {
                    if (isHeader(paragraph, 1)) {
                        sectorId++
                        for (i in nodes.size..sectorId) {
                            nodes.add(Node(paragraph))
                        }
                        nodes[sectorId].header = paragraphs[paragraph] as P
                    }
                }
                is JAXBElement<*> -> {
                    if ((paragraphs[paragraph] as JAXBElement<*>).value is Tbl) {
                        tables.add((paragraphs[paragraph] as JAXBElement<*>).value as Tbl)
                    }
                }
            }
            if (nodes.size <= sectorId) {
                nodes.add(Node(paragraph))
            }
            nodes[sectorId].add(paragraphs[paragraph])
            paragraph++
        }
    }

    fun detectNodes() {
        for (node in 0 until nodes.size) {
            if (nodes[node][0] is P) {
                if (nodes[node].header == null) {
                    nodes[node].type = NodeType.FRONT_PAGE
                    continue
                }
                nodes[node].type = detectNodeType(TextUtils.getText(nodes[node].header), node)
            }
        }
        if (nodes[0].type == null) {
            nodes[0].type = NodeType.FRONT_PAGE
        }
    }

    private fun detectNodeType(text: String, chapterId: Int): NodeType {
        if (text.split(Regex("\\s+"))[0].matches(Regex("^(?:\\d{1,2}\\.?){1,3}$"))) {
            return NodeType.BODY
        }
        for (keys in 0 until keywords.keywordsBySector.size) {
            if (keywords.keywordsBySector[keys].contains(text.uppercase())) {
                return NodeType.values()[keys]
            }
        }
        errors.add(DocumentError(chapterId.toLong(), ErrorType.CHAPTER_INCORRECT, documentId))
        return NodeType.UNDEFINED
    }

    fun findIncorrectNodes() {
        try {
            if (nodes[0].type != NodeType.FRONT_PAGE) {
                errors.add(DocumentError(0.toLong(), ErrorType.CHAPTER_FRONT_PAGE_NOT_FOUND, documentId))
            }
            if (nodes[1].type != NodeType.ANNOTATION) {
                errors.add(DocumentError(1.toLong(), ErrorType.CHAPTER_ANNOTATION_NOT_FOUND, documentId))
            }
            if (nodes[2].type != NodeType.CONTENTS) {
                errors.add(DocumentError(2.toLong(), ErrorType.CHAPTER_CONTENTS_NOT_FOUNDS, documentId))
            }
            if (nodes[3].type != NodeType.INTRODUCTION) {
                errors.add(DocumentError(3.toLong(), ErrorType.CHAPTER_INTRODUCTION_NOT_FOUND, documentId))
            }
            if (nodes[4].type != NodeType.BODY) {
                errors.add(DocumentError(4.toLong(), ErrorType.CHAPTER_BODY_NOT_FOUND, documentId))
            }
            var i = 4
            while (nodes[i].type == NodeType.BODY) {
                i++
            }
            if (nodes[i].type != NodeType.CONCLUSION) {
                errors.add(DocumentError(i.toLong(), ErrorType.CHAPTER_CONCLUSION_NOT_FOUND, documentId))
            }
            if (nodes[i + 1].type != NodeType.REFERENCES) {
                errors.add(DocumentError((i + 1).toLong(), ErrorType.CHAPTER_REFERENCES_NOT_FOUND, documentId))
            }
            if (nodes[i + 2].type != NodeType.APPENDIX) {
                errors.add(DocumentError((i + 2).toLong(), ErrorType.CHAPTER_APPENDIX_NOT_FOUND, documentId))
            }
        } catch (e: IndexOutOfBoundsException) {
            errors.add(DocumentError(ErrorType.CHAPTER_COUNT_MISMATCH, documentId))
        }
    }

    private fun isHeader(paragraph: Int, level: Int): Boolean {
        val pPr = resolver.getEffectivePPr((document.content[paragraph] as P).pPr)
        if (pPr == null || pPr.outlineLvl == null) {
            return false
        }
        return pPr.outlineLvl.`val`.toInt() == level - 1
    }

    fun findGeneralAllErrors(p: Int) {
        findGeneralAllErrors(p, resolver.getEffectivePPr((document.content[p] as P).pPr))
    }

    private fun findGeneralAllErrors(p: Int, pPr: PPr) {
        fun findGeneralPErrors(pPr: PPr, p: Int) {
            if (pPr.textAlignment != null && pPr.textAlignment.`val` != "left") {
                errors.add(DocumentError(p, -1, ErrorType.INCORRECT_TEXT_DIRECTION, documentId))
            }
            if (pPr.pBdr != null) {
                errors.add(DocumentError(p, -1, ErrorType.BORDER, documentId))
            }
            if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
                errors.add(DocumentError(p, -1, ErrorType.BACKGROUND_FILLED, documentId))
            }
        }

        fun findGeneralRErrors(rPr: RPr, p: Int, r: Int) {
            if (rPr.rFonts.ascii != "Times New Roman") {
                errors.add(DocumentError(p, r, ErrorType.INCORRECT_TEXT_FONT, documentId))
            }
            if (rPr.color != null && rPr.color.`val` != "FFFFFF" && rPr.color.`val` != "auto") {
                errors.add(DocumentError(p, r, ErrorType.INCORRECT_TEXT_COLOR, documentId))
            }
            if (rPr.sz.`val`.toInt() / 2 != 14) {
                errors.add(DocumentError(p, r, ErrorType.INCORRECT_FONT_SIZE, documentId))
            }
            if (!(rPr.i == null || !rPr.i.isVal)) {
                errors.add(DocumentError(p, r, ErrorType.ITALIC_TEXT, documentId))
            }
            if (!(rPr.strike == null || !rPr.strike.isVal)) {
                errors.add(DocumentError(p, r, ErrorType.STRIKETHROUGH, documentId))
            }
            if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
                errors.add(DocumentError(p, r, ErrorType.HIGHLIGHT, documentId))
            }
        }

        val paragraph = document.content[p] as P
        findGeneralPErrors(pPr, p)
        for (run in 0 until paragraph.content.size) {
            val rPr = resolver.getEffectiveRPr((paragraph.content[run] as R).rPr, paragraph.pPr)
            findGeneralRErrors(rPr, p, run)
        }
    }

    fun findHeaderAllErrors(paragraph: Int) {
        val p = document.content[paragraph] as P
        val pPr = resolver.getEffectivePPr(p.pPr)
        if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
            errors.add(DocumentError(paragraph, 0, ErrorType.INCORRECT_HEADER_ALIGNMENT, documentId))
        }
        val run = p.content[0] as R
        val rPr = resolver.getEffectiveRPr(run.rPr, pPr)
        val text = TextUtils.getText(run)
        if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
            errors.add(DocumentError(paragraph, 0, ErrorType.HEADER_IS_NOT_UPPERCASE, documentId))
        }
        findGeneralAllErrors(paragraph, pPr)
    }

    fun findRegularTextAllErrors(paragraph: Int) {
        fun findRegularTextPErrors(pPr: PPr, isEmpty: Boolean) {
            if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
                errors.add(
                    DocumentError(
                        paragraph, -1,
                        if (!isEmpty) ErrorType.INCORRECT_REGULAR_TEXT_ALIGNMENT
                        else ErrorType.WHITESPACE_INCORRECT_ALIGNMENT, documentId
                    )
                )
            }
        }

        fun findRegularTextRErrors(rPr: RPr, run: Int, isEmpty: Boolean) {
            if (rPr.b != null && !rPr.b.isVal) {
                errors.add(
                    DocumentError(
                        paragraph, run,
                        if (!isEmpty) ErrorType.REGULAR_TEXT_WAS_BOLD else ErrorType.WHITESPACE_BOLD, documentId
                    )
                )
            }
            if (rPr.u != null && rPr.u.`val`.value() != "none") {
                errors.add(
                    DocumentError(
                        paragraph, run,
                        if (!isEmpty) ErrorType.REGULAR_TEXT_WAS_UNDERLINED else ErrorType.WHITESPACE_UNDERLINED, documentId
                    )
                )
            }
        }

        val p = document.content[paragraph] as P
        val pPr = resolver.getEffectivePPr(p.pPr)
        val isEmpty = TextUtils.getText(p).isEmpty()
        findRegularTextPErrors(pPr, isEmpty)

        for (run in 0 until p.content.size) {
            val rPr = resolver.getEffectiveRPr((p.content[run] as R).rPr, p.pPr)
            findRegularTextRErrors(rPr, run, isEmpty)
        }

        findGeneralAllErrors(paragraph, pPr)
    }

    fun checkAnnotation() {
        val node = nodes.first { it.type == NodeType.ANNOTATION }
        val paragraphs = node.content
        for (paragraph in 1 until paragraphs.size) {
            if (paragraphs[paragraph] !is P) {
                errors.add(DocumentError(node.startPos + paragraph, ErrorType.ANNOTATION_MUST_NOT_CONTAINS_MEDIA, documentId))
            } else {
                findRegularTextAllErrors(node.startPos + paragraph)
            }
        }
    }
}