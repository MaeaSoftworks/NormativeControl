package com.prmncr.docx4nc

import com.prmncr.normativecontrol.components.CorrectDocumentParams
import com.prmncr.normativecontrol.components.SectorKeywords
import com.prmncr.normativecontrol.dtos.Error
import com.prmncr.normativecontrol.dtos.ErrorType
import org.docx4j.TextUtils
import org.docx4j.model.styles.StyleTree
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.Br
import org.docx4j.wml.P
import org.docx4j.wml.R
import java.math.BigInteger
import java.util.*
import java.util.regex.Pattern

class DocumentParser(
    private var document: WordprocessingMLPackage,
    private var params: CorrectDocumentParams,
    private var keywords: SectorKeywords
) {
    private val points: Int = 20
    private var mainDocumentPart: MainDocumentPart = document.mainDocumentPart

    private var frontPage: MutableList<Any> = ArrayList()
    private var annotation: MutableList<Any> = ArrayList()
    private var contents: MutableList<Any> = ArrayList()
    private var introduction: MutableList<Any> = ArrayList()
    private var body: MutableList<Any> = ArrayList()
    private var conclusion: MutableList<Any> = ArrayList()
    private var references: MutableList<Any> = ArrayList()
    private var appendix: MutableList<Any> = ArrayList()
    var sectors: MutableList<MutableList<Any>> = ArrayList()
    var errors: MutableList<Error> = ArrayList()
    private var styles: StyleTree

    init {
        sectors.add(frontPage)
        sectors.add(annotation)
        sectors.add(contents)
        sectors.add(introduction)
        sectors.add(body)
        sectors.add(conclusion)
        sectors.add(references)
        sectors.add(appendix)
        styles = mainDocumentPart.styleTree
    }

    fun runStyleCheck(): List<Error> {
        findSectors(0, 0)
        checkPageSize()
        checkPageMargins()
        return errors
    }

    private fun getLongPixels(points: BigInteger): Long {
        return points.toLong() / this.points
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
        val marginTop = pageMargins.top.toDouble() / 20
        val marginLeft = pageMargins.left.toDouble() / 20
        val marginBottom = pageMargins.bottom.toDouble() / 20
        val marginRight = pageMargins.right.toDouble() / 20

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
            val p = paragraphs[paragraph]
            if (p is P && p.content.size <= 2) {
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
        val words = text.split(Pattern.compile("\\s+"))
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
        if (!p.pPr.pStyle.getVal().equals("Heading1")) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.BUILT_IN_HEADER_STYLE_IS_NOT_USED))
        }
        val run = p.content[0] as R
        val text = TextUtils.getText(run)
        if (text.uppercase() != text) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.HEADER_IS_NOT_UPPER_CASE))
        }
        if (p.pPr.jc == null || !Objects.equals(p.pPr.jc.getVal().value(), "center")) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.INCORRECT_HEADER_ALIGNMENT))
        }
        checkInvalidProperties(run, paragraph)
    }

    private fun checkInvalidProperties(run: R, paragraph: Int) {
        val rStyle = StyleChecker.defineRStyle(run, styles)
        if (TextUtils.getText(run).isNotEmpty()
            && StyleChecker.isFontWrong(mainDocumentPart.content[paragraph] as P, run, styles, rStyle)) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.INCORRECT_FONT));
        }

        if (!Objects.equals(run.rPr.color.getVal(), "FFFFFF")
            && !Objects.equals(run.rPr.color.getVal(), "auto")
        ) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.INCORRECT_TEXT_COLOR))
        }
        if (run.rPr.sz == null || run.rPr.sz.getVal().toInt() / 2 != 14) {
            errors.add(Error(paragraph.toLong(), 0, ErrorType.INCORRECT_FONT_SIZE))
        }
    }
}