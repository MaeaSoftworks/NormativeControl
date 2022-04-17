package com.prmncr.normativecontrol.services

import com.prmncr.normativecontrol.components.CorrectDocumentParams
import com.prmncr.normativecontrol.components.SectorKeywords
import com.prmncr.normativecontrol.dtos.Error
import com.prmncr.normativecontrol.dtos.ErrorType
import com.prmncr.normativecontrol.dtos.Prs
import org.apache.commons.lang3.reflect.FieldUtils
import org.docx4j.TextUtils
import org.docx4j.model.styles.StyleTree
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
    private var styleTree: StyleTree = document.mainDocumentPart.styleTree
    private val styles: MutableMap<String, Prs> = HashMap()

    init {
        for (i in 0..7) {
            sectors.add(ArrayList<Any>())
        }
        createStyleMap()
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
        val marginTop = getLongPixels(pageMargins.top)
        val marginLeft = getLongPixels(pageMargins.left)
        val marginBottom = getLongPixels(pageMargins.bottom)
        val marginRight = getLongPixels(pageMargins.right)
        if (marginTop.toDouble() != params.pageMarginTop
            || marginRight.toDouble() != params.pageMarginRight
            || marginBottom.toDouble() != params.pageMarginBottom
            || marginLeft.toDouble() != params.pageMarginLeft
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
        val run = p.content[0] as R
        val text = TextUtils.getText(run)
        if (text.uppercase(Locale.getDefault()) != text) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.HEADER_IS_NOT_UPPER_CASE))
        }
        checkInvalidProperties(run, paragraph)
    }

    private fun checkInvalidProperties(run: R, paragraph: Int) {
        if (run.rPr == null) {
            return
        }
        if (run.rPr.rFonts.ascii != "Times New Roman") {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.INCORRECT_HEADER_FONT))
        }
        if (run.rPr.color.getVal() != "FFFFFF"
            && run.rPr.color.getVal() != "auto"
        ) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.INCORRECT_TEXT_COLOR))
        }
        if (run.rPr.sz.getVal().toInt() / 2 != 14) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.INCORRECT_TEXT_COLOR))
        }
    }

    @Throws(IllegalAccessException::class)
    private fun createStyleMap() {
        val nodesP = styleTree.paragraphStylesTree.toList()
        // todo add character style props overwriting rules
        // val nodesR = styleTree.getCharacterStylesTree().toList();
        var defaultRPr: RPr? = null
        var defaultPPr: PPr? = null
        for (style in nodesP) {
            if (FieldUtils.readField(style, "name", true) == "DocDefaults") {
                defaultRPr = style.data.style.rPr
                defaultPPr = style.data.style.pPr
            } else {
                styles[(FieldUtils.readField(style, "name", true) as String)] =
                    Prs(
                        if (style.data.style.rPr == null) defaultRPr else style.data.style.rPr,
                        if (style.data.style.pPr == null) defaultPPr else style.data.style.pPr
                    )
            }
        }
    }

    fun detectRStyle(paragraph: Int, run: Int): RPr {
        val rpr = RPr()
        val r = (mainDocumentPart.content[paragraph] as P).content[run] as R

        // font size
        if (r.rPr == null) {
            if ((r.parent as P).pPr.rPr == null) {
                // get style from style tree
            } else {
                // get from p
            }
        } else {
            if (r.rPr.sz != null) {
                rpr.sz = r.rPr.sz
            } else {
                if ((r.parent as P).pPr.rPr.sz != null) {
                    rpr.sz = (r.parent as P).pPr.rPr.sz
                } else {
                    if (styleTree.characterStylesTree[(r.parent as P).pPr.pStyle.getVal()] != null) {
                    }
                }
            }
        }
        return rpr
    }
}