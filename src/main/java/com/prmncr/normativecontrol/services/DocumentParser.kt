package com.prmncr.normativecontrol.services

import com.prmncr.normativecontrol.components.CorrectDocumentParams
import com.prmncr.normativecontrol.components.HeadersKeywords
import com.prmncr.normativecontrol.dtos.Error
import com.prmncr.normativecontrol.dtos.ErrorType
import com.prmncr.normativecontrol.dtos.Node
import com.prmncr.normativecontrol.dtos.NodeType
import org.docx4j.TextUtils
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*

class DocumentParser(
    mlPackage: WordprocessingMLPackage,
    private var params: CorrectDocumentParams,
    private var keywords: HeadersKeywords
) {
    private val document: MainDocumentPart = mlPackage.mainDocumentPart
    private val resolver: PropertyResolver = PropertyResolver(mlPackage)
    val nodes: MutableList<Node> = ArrayList()
    val errors: MutableList<Error> = ArrayList()

    fun runStyleCheck(): List<Error> {
        findSectors()
        checkPageSize()
        checkPageMargins()
        detectNodes()
        return errors
    }

    fun detectNodes() {
        for (node in nodes) {
            if (node[0] is P) {
                if (node.header == null) {
                    node.type = NodeType.FRONT_PAGE
                    continue
                }
                node.type = detectNodeType(TextUtils.getText(node.header))
            }
        }
        if (nodes[0].type == null) {
            nodes[0].type = NodeType.FRONT_PAGE
        }
    }

    private fun detectNodeType(text: String): NodeType {
        if (text.split(Regex("\\s+"))[0].matches(Regex("^(?:\\d{1,2}\\.?){1,3}$"))) {
            return NodeType.BODY
        }
        for (keys in 0 until keywords.keywordsBySector.size) {
            if (keywords.keywordsBySector[keys].contains(text)) {
                return NodeType.values()[keys]
            }
        }
        errors.add(Error(-1, -1, ErrorType.INCORRECT_SECTORS))
        return NodeType.UNDEFINED
    }

    fun checkPageSize() {
        val pageSize = document.jaxbElement.body.sectPr.pgSz
        if (pageSize.w != params.pageWidth || pageSize.h != params.pageHeight) {
            errors.add(Error(-1, -1, ErrorType.INCORRECT_PAGE_SIZE))
        }
    }

    fun checkPageMargins() {
        val pageMargins = document.jaxbElement.body.sectPr.pgMar
        if (pageMargins.top != params.pageMarginTop || pageMargins.right != params.pageMarginRight
            || pageMargins.bottom != params.pageMarginBottom || pageMargins.left != params.pageMarginLeft
        ) {
            errors.add(Error(-1, -1, ErrorType.INCORRECT_PAGE_MARGINS))
        }
    }

    fun findSectors() {
        val paragraphs = document.content
        var paragraph = 0
        var sectorId = 0
        while (paragraph < paragraphs.size) {
            if (paragraphs[paragraph] is P && isHeader(paragraph, 1)) {
                sectorId++
                for (i in nodes.size..sectorId) {
                    nodes.add(Node())
                }
                nodes[sectorId].header = paragraphs[paragraph] as P
            } else {
                if (nodes.size <= sectorId) {
                    nodes.add(Node())
                }
                nodes[sectorId].add(paragraphs[paragraph])
            }
            paragraph++
        }
        if (nodes.size < 8) {
            errors.add(Error(-1, -1, ErrorType.INCORRECT_SECTORS))
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
                errors.add(Error(p, -1, ErrorType.INCORRECT_TEXT_DIRECTION))
            }
            if (pPr.pBdr != null) {
                errors.add(Error(p, -1, ErrorType.BORDER))
            }
            if (pPr.shd != null && pPr.shd.fill != null && pPr.shd.fill != "FFFFFF") {
                errors.add(Error(p, -1, ErrorType.BACKGROUND_FILLED))
            }
        }

        fun findGeneralRErrors(rPr: RPr, p: Int, r: Int) {
            if (rPr.rFonts.ascii != "Times New Roman") {
                errors.add(Error(p, r, ErrorType.INCORRECT_TEXT_FONT))
            }
            if (rPr.color != null && rPr.color.`val` != "FFFFFF" && rPr.color.`val` != "auto") {
                errors.add(Error(p, r, ErrorType.INCORRECT_TEXT_COLOR))
            }
            if (rPr.sz.`val`.toInt() / 2 != 14) {
                errors.add(Error(p, r, ErrorType.INCORRECT_FONT_SIZE))
            }
            if (!(rPr.i == null || !rPr.i.isVal)) {
                errors.add(Error(p, r, ErrorType.ITALIC_TEXT))
            }
            if (!(rPr.strike == null || !rPr.strike.isVal)) {
                errors.add(Error(p, r, ErrorType.STRIKETHROUGH))
            }
            if (!(rPr.highlight == null || rPr.highlight.`val` == "white")) {
                errors.add(Error(p, r, ErrorType.HIGHLIGHT))
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
            errors.add(Error(paragraph, 0, ErrorType.INCORRECT_HEADER_ALIGNMENT))
        }
        val run = p.content[0] as R
        val rPr = resolver.getEffectiveRPr(run.rPr, pPr)
        val text = TextUtils.getText(run)
        if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
            errors.add(Error(paragraph, 0, ErrorType.HEADER_IS_NOT_UPPERCASE))
        }
        findGeneralAllErrors(paragraph, pPr)
    }

    fun findRegularTextAllErrors(paragraph: Int) {
        fun findRegularTextPErrors(pPr: PPr) {
            if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.BOTH) {
                errors.add(Error(paragraph, -1, ErrorType.INCORRECT_REGULAR_TEXT_ALIGNMENT))
            }
        }

        fun findRegularTextRErrors(rPr: RPr, run: Int) {
            if (rPr.b.isVal) {
                errors.add(Error(paragraph, run, ErrorType.REGULAR_TEXT_WAS_BOLD))
            }
            if (rPr.u.`val`.value() != "none") {
                errors.add(Error(paragraph, run, ErrorType.REGULAR_TEXT_WAS_UNDERLINED))
            }
        }

        val p = document.content[paragraph] as P
        val pPr = resolver.getEffectivePPr(p.pPr)

        findRegularTextPErrors(pPr)

        for (run in 0 until p.content.size) {
            val rPr = resolver.getEffectiveRPr((p.content[run] as R).rPr, p.pPr)
            findRegularTextRErrors(rPr, run)
        }

        findGeneralAllErrors(paragraph, pPr)
    }
}