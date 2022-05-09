package com.maeasoftworks.normativecontrol.dtos.chapters

import com.maeasoftworks.normativecontrol.components.HeadersKeywords
import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.dtos.Chapter
import com.maeasoftworks.normativecontrol.dtos.Document
import com.maeasoftworks.normativecontrol.dtos.DocumentParser
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType.*
import org.docx4j.TextUtils
import org.docx4j.model.PropertyResolver
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import javax.xml.bind.JAXBElement
import kotlin.math.abs
import kotlin.math.floor

abstract class ChapterParser(
    override val document: Document,
    override val keywords: HeadersKeywords,
    open val chapter: Chapter
) : DocumentParser(document, keywords) {
    final override lateinit var mlPackage: WordprocessingMLPackage
    final override lateinit var mainDocumentPart: MainDocumentPart
    final override lateinit var resolver: PropertyResolver
    final override var parsers: MutableList<ChapterParser?> = ArrayList()
    final override var errors: MutableList<DocumentError> = ArrayList()
    final override var tables: MutableList<Tbl> = ArrayList()
    final override var pictures: MutableList<Any> = ArrayList()

    constructor(parser: DocumentParser, chapter: Chapter) : this(parser.document, parser.keywords, chapter) {
        mlPackage = parser.mlPackage
        mainDocumentPart = parser.mainDocumentPart
        resolver = parser.resolver
        parsers = parser.parsers
        errors = parser.errors
        tables = parser.tables
        pictures = parser.pictures
    }

    abstract fun parse()

    fun findCommonPRErrors(p: Int) {
        findCommonPRErrors(p, resolver.getEffectivePPr((mainDocumentPart.content[p] as P).pPr))
    }

    protected open fun findCommonPRErrors(p: Int, pPr: PPr) {
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
        if (pPr.spacing != null && pPr.spacing.line != null && pPr.spacing.line.intValueExact() != 240) {
            errors += DocumentError(document.id, paragraph, 0, TEXT_HEADER_LINE_SPACING)
        }
        val run = if (p.content[0] is JAXBElement<*>) {
            p.content[1] as R
        } else {
            p.content[0] as R
        }
        val rPr = resolver.getEffectiveRPr(run.rPr, pPr)
        val text = TextUtils.getText(p)
        if (!(text.uppercase() == text || (rPr.caps != null && rPr.caps.isVal))) {
            errors += DocumentError(document.id, paragraph, 0, TEXT_HEADER_NOT_UPPERCASE)
        }
        if (text.endsWith(".")) {
            errors += DocumentError(document.id, paragraph, -1, TEXT_HEADER_REDUNDANT_DOT)
        }
        if (rPr.b == null || !rPr.b.isVal) {
            errors += DocumentError(document.id, paragraph, 0, TEXT_HEADER_NOT_BOLD)
        }
        findCommonPRErrors(paragraph, pPr)
    }

    protected fun findRegularTextPRErrors(paragraph: Int) {
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
                        if (isEmpty) TEXT_WHITESPACE_LINE_SPACING else TEXT_REGULAR_LINE_SPACING
                    )
                }
            }
            if (pPr.numPr != null) {
                validateList(paragraph)
            } else {
                if (pPr.ind != null && abs(floor(pPr.ind.firstLine.intValueExact() / 1440 * 2.54) - 1.25) <= 0.01) {
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
        }

        fun findRegularTextRErrors(rPr: RPr, run: Int, isEmpty: Boolean) {
            if (rPr.b != null && !rPr.b.isVal) {
                errors += DocumentError(
                    document.id, paragraph,
                    run,
                    if (isEmpty) TEXT_WHITESPACE_BOLD else TEXT_REGULAR_WAS_BOLD
                )
            }
            if (rPr.caps != null && !rPr.caps.isVal) {
                errors += DocumentError(
                    document.id, paragraph,
                    run,
                    if (isEmpty) TEXT_WHITESPACE_UPPERCASE else TEXT_REGULAR_UPPERCASE
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

    fun validateList(startParagraph: Int) {
        var end = startParagraph
        val list = ArrayList<P>()
        while (mainDocumentPart.content.size > end
            && mainDocumentPart.content[end] is P
            && (mainDocumentPart.content[end] as P).pPr.numPr != null) {
            list += mainDocumentPart.content[end] as P
            end++
        }
    }

}