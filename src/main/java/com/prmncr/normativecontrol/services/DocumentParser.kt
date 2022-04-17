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

    fun findSectors(paragraphId: Int, sectorId: Int) {
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
                    val error = Error(paragraph.toLong(), -1, ErrorType.INCORRECT_SECTORS)
                    for (i in sectorId + 1..keywords.allKeywords.size) {
                        // what sector is this header?
                        if (keywords.allKeywords[i - 1].contains(text.uppercase())) {
                            // we found next sector
                            checkHeaderStyle(paragraph)
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

    fun isHeader(paragraph: Int, text: String): Boolean {
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

    fun checkHeaderStyle(paragraph: Int) {
        val p = mainDocumentPart.content[paragraph] as P
        if (p.pPr.pStyle.getVal() != "Heading1") {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.BUILT_IN_HEADER_STYLE_IS_NOT_USED))
        }
        val pPr = resolver.getEffectivePPr(p.pPr)
        if (pPr.jc == null || pPr.jc.`val` != JcEnumeration.CENTER) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.INCORRECT_HEADER_ALIGNMENT))
        }
        val rPr = resolver.getEffectiveRPr((p.content[0] as R).rPr, p.pPr)
        val run = p.content[0] as R
        val text = TextUtils.getText(run)
        if (text.uppercase() != text) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.HEADER_IS_NOT_UPPERCASE))
        }
        findIncorrectProperties(rPr, pPr, paragraph.toLong(), 0)
    }

    private fun findIncorrectProperties(rPr: RPr, pPr: PPr, p: Long, r: Long) {
        if (rPr.rFonts.ascii != "Times New Roman") {
            errors.add(Error(p, r, ErrorType.INCORRECT_TEXT_FONT))
        }
        if (rPr.color != null && rPr.color.`val` != "FFFFFF" && rPr.color.`val` != "auto") {
            errors.add(Error(p, r, ErrorType.INCORRECT_TEXT_COLOR))
        }
        if (rPr.sz.`val`.toInt() / 2 != 14) {
            errors.add(Error(p, r, ErrorType.INCORRECT_FONT_SIZE))
        }

        // todo add other checks
    }
}