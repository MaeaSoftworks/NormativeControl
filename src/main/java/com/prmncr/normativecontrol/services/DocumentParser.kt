package com.prmncr.normativecontrol.services

import com.prmncr.normativecontrol.components.CorrectDocumentParams
import com.prmncr.normativecontrol.components.SectorKeywords
import com.prmncr.normativecontrol.dtos.Error
import com.prmncr.normativecontrol.dtos.ErrorType
import org.docx4j.TextUtils
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import java.math.BigInteger
import java.util.*

class DocumentParser @Throws(IllegalAccessException::class) constructor(
    private var document: WordprocessingMLPackage,
    private var params: CorrectDocumentParams,
    private var keywords: SectorKeywords
) {
    private val points = 20
    private var mainDocumentPart: MainDocumentPart = document.mainDocumentPart
    @JvmField
    val sectors: MutableList<MutableList<Any>> = ArrayList()
    @JvmField
    val errors: MutableList<Error> = ArrayList()
    private val resolver: PropertyResolver

    init {
        for (i in 0..7) {
            sectors.add(ArrayList<Any>())
        }
        resolver = PropertyResolver(document)
    }

    fun runStyleCheck(): List<Error?> {
        findSectors(0, 0)
        checkPageSize()
        checkPageMargins()
        return errors
    }

    private fun getLongPixels(points: Any): Long {
        return (points as BigInteger).toLong() / this.points
    }

    fun checkPageSize() {
        val sector = mainDocumentPart.jaxbElement.body.sectPr
        val pageSize = sector.pgSz
        val width = getLongPixels(pageSize.w)
        val height = getLongPixels(pageSize.h)
        if (width != params.pageWidth || height != params.pageHeight) {
            errors.add(Error(-1, -1, ErrorType.INCORRECT_PAGE_SIZE))
        }
    }

    fun checkPageMargins() {
        val sector = mainDocumentPart.jaxbElement.body.sectPr
        val pageMargins = sector.pgMar
        val marginTop = pageMargins.top.toDouble() / points
        val marginLeft = pageMargins.left.toDouble() / points
        val marginBottom =pageMargins.bottom.toDouble() / points
        val marginRight = pageMargins.right.toDouble() / points
        if (marginTop != params.pageMarginTop || marginRight != params.pageMarginRight
            || marginBottom != params.pageMarginBottom || marginLeft != params.pageMarginLeft
        ) {
            errors.add(Error(-1, -1, ErrorType.INCORRECT_PAGE_MARGINS))
        }
    }

    fun findSectors() {
        findSectors(0, 0)
    }

    private fun findSectors(paragraphId: Int, sectorId: Int) {
        val paragraphs = mainDocumentPart.content
        var paragraph = paragraphId
        while (paragraph < paragraphs.size) {
            // are we collecting last sector? (he hasn't got next header)
            if (keywords.allKeywords.size + 1 == sectorId) {
                sectors[sectorId].add(paragraphs[paragraph])
                continue
            }
            if (paragraphs[paragraph] is P && (paragraphs[paragraph] as P).content.size <= 2) {
                val text = TextUtils.getText(paragraphs[paragraph])
                // did we find some header?
                if (isHeader(paragraph, text)) {
                    // yes, some header is here
                    val error = Error(paragraph, -1, ErrorType.INCORRECT_SECTORS)
                    for (i in sectorId + 1..keywords.allKeywords.size) {
                        // what sector is this header?
                        if (keywords.allKeywords[i - 1].contains(text.uppercase())) {
                            // we found next sector
                            findHeaderAllErrors(paragraph)
                            paragraph++
                            findSectors(paragraph, i)
                            return
                        } else if (!errors.contains(error)) {
                            // it is not next sector! error!
                            errors.add(error)
                        }
                    }
                } else {
                    //it's not a header
                    sectors[sectorId].add(paragraphs[paragraph])
                }
            } else {
                sectors[sectorId].add(paragraphs[paragraph])
            }
            paragraph++
        }
    }

    private fun isHeader(paragraph: Int, text: String): Boolean {
        val words = text.split("\\s+")
        if (words.isEmpty() || words.size > keywords.maxLength) {
            return false
        }

        val paragraphs = mainDocumentPart.content
        if (paragraph > 0) {
            val previous = paragraphs[paragraph - 1]
            if (previous is P) {
                val runs = previous.content
                if (runs.isNotEmpty()) {
                    val lastRun = runs[runs.size - 1]
                    if (lastRun is R) {
                        if (lastRun.content[lastRun.content.size - 1] is Br) {
                            return true
                        }
                    }
                    if (lastRun !is Br) {
                        return false
                    }
                }
            }
        }
        return keywords.allKeywordsFlat.contains(text.uppercase())
    }

    fun findGeneralAllErrors(p: Int) {
        findGeneralAllErrors(p, resolver.getEffectivePPr((mainDocumentPart.content[p] as P).pPr))
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

        val paragraph = mainDocumentPart.content[p] as P
        findGeneralPErrors(pPr, p)
        for (run in 0 until paragraph.content.size) {
            val rPr = resolver.getEffectiveRPr((paragraph.content[run] as R).rPr, paragraph.pPr)
            findGeneralRErrors(rPr, p, run)
        }
    }

    fun findHeaderAllErrors(paragraph: Int) {
        val p = mainDocumentPart.content[paragraph] as P
        if (p.pPr.pStyle.getVal() != "Heading1") {
            errors.add(Error(paragraph, 0, ErrorType.BUILT_IN_HEADER_STYLE_IS_NOT_USED))
        }
        val pPr = resolver.getEffectivePPr(p.pPr)
        if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
            errors.add(Error(paragraph, 0, ErrorType.INCORRECT_HEADER_ALIGNMENT))
        }
        val run = p.content[0] as R
        val text = TextUtils.getText(run)
        if (text.uppercase() != text) {
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

        val p = mainDocumentPart.content[paragraph] as P
        val pPr = resolver.getEffectivePPr(p.pPr)

        findRegularTextPErrors(pPr)

        for (run in 0 until p.content.size) {
            val rPr = resolver.getEffectiveRPr((p.content[run] as R).rPr, p.pPr)
            findRegularTextRErrors(rPr, run)
        }

        findGeneralAllErrors(paragraph, pPr)
    }
}