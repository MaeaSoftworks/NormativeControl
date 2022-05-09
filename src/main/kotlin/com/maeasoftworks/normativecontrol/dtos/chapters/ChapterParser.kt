package com.maeasoftworks.normativecontrol.dtos.chapters

import com.maeasoftworks.normativecontrol.components.HeadersKeywords
import com.maeasoftworks.normativecontrol.daos.DocumentError
import com.maeasoftworks.normativecontrol.dtos.Chapter
import com.maeasoftworks.normativecontrol.dtos.Document
import com.maeasoftworks.normativecontrol.dtos.DocumentParser
import com.maeasoftworks.normativecontrol.dtos.enums.ErrorType.*
import org.docx4j.TextUtils
import org.docx4j.wml.*
import javax.xml.bind.JAXBElement
import kotlin.math.abs
import kotlin.math.floor

abstract class ChapterParser(
    override val document: Document,
    override val keywords: HeadersKeywords,
    protected val chapter: Chapter
) : DocumentParser(document, keywords) {
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

    /*
    fun findCommonPRErrors(p: Int) {
        findCommonPRErrors(p, resolver.getEffectivePPr((mainDocumentPart.content[p] as P).pPr))
    }
    */

    protected open fun launchCommonPErrorsFinder(
        pPr: PPr,
        p: Int,
        isEmpty: Boolean,
        pFunctions: Array<(documentId: String,
                           p: Int,
                           isEmpty: Boolean,
                           pPr: PPr) -> DocumentError?>
    ) {
        for (func in pFunctions) {
            val error = func(document.id, p, isEmpty, pPr)
            if (error != null) {
                errors += error
            }
        }
    }

    protected open fun findCommonRErrors(
        rPr: RPr,
        p: Int,
        r: Int,
        isEmpty: Boolean,
        functions: Array<(documentId: String,
                          rPr: RPr,
                          p: Int,
                          r: Int,
                          isEmpty: Boolean) -> DocumentError?>
    ) {
        for (func in functions) {
            val error = func(document.id, rPr, p, r, isEmpty)
            if (error != null) {
                errors += error
            }
        }
    }

    open fun findCommonPRErrors(
        p: Int,
        pPr: PPr,
        pFunctions: Array<(documentId: String,
               p: Int,
               isEmpty: Boolean,
               pPr: PPr) -> DocumentError?>,
        rFunctions: Array<(documentId: String,
                           rPr: RPr,
                           p: Int,
                           r: Int,
                           isEmpty: Boolean) -> DocumentError?>,
        commonPFinder: (pPr: PPr,
                        p: Int,
                        isEmpty: Boolean,
                        Array<(documentId: String,
                              p: Int,
                              isEmpty: Boolean,
                              pPr: PPr) -> DocumentError?>) -> Unit = this::launchCommonPErrorsFinder,
        commonRFinder: (rPr: RPr,
                        p: Int,
                        r: Int,
                        isEmpty: Boolean,
                        Array<(documentId: String,
                               rPr: RPr,
                               p: Int,
                               r: Int,
                               isEmpty: Boolean) -> DocumentError?>) -> Unit = this::findCommonRErrors
    ) {
        val paragraph = mainDocumentPart.content[p] as P
        val text = TextUtils.getText(p)
        val isEmpty = text?.isEmpty() ?: false
        commonPFinder(pPr, p, isEmpty, pFunctions)
        for (run in 0 until paragraph.content.size) {
            if (paragraph.content[run] is R) {
                val rPr = resolver.getEffectiveRPr((paragraph.content[run] as R).rPr, paragraph.pPr)
                commonRFinder(rPr, p, run, isEmpty, rFunctions)
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
    }

    protected open fun findRegularPErrors(paragraph: Int, pPr: PPr, isEmpty: Boolean) {
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

    protected open fun findRegularRErrors(paragraph: Int, rPr: RPr, run: Int, isEmpty: Boolean) {
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

    protected fun findRegularPRErrors(
        paragraph: Int,
        regularPFinder: (paragraph: Int, pPr: PPr, isEmpty: Boolean) -> Unit = this::findRegularPErrors,
        regularRFinder: (paragraph: Int, rPr: RPr, run: Int, isEmpty: Boolean) -> Unit = this::findRegularRErrors
        ) {
        val p = mainDocumentPart.content[paragraph] as P
        val pPr = resolver.getEffectivePPr(p.pPr)
        val isEmpty = TextUtils.getText(p).isEmpty()
        regularPFinder(paragraph, pPr, isEmpty)

        for (run in 0 until p.content.size) {
            if (p.content[run] is R) {
                val rPr = resolver.getEffectiveRPr((p.content[run] as R).rPr, p.pPr)
                regularRFinder(paragraph, rPr, run, isEmpty)
            }
        }
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