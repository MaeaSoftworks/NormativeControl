package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.Document
import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.chapters.HeadersKeywords
import org.docx4j.TextUtils
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr

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

    protected open fun launchBasePErrorFinder(
        pPr: PPr,
        p: Int,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart,
        pFunctions: Array<(
            documentId: String,
            p: Int,
            isEmpty: Boolean,
            pPr: PPr,
            mainDocumentPart: MainDocumentPart
        ) -> DocumentError?>
    ) {
        for (func in pFunctions) {
            val error = func(document.id, p, isEmpty, pPr, mainDocumentPart)
            if (error != null) {
                errors += error
            }
        }
    }

    protected open fun launchBaseRErrorFinder(
        rPr: RPr,
        p: Int,
        r: Int,
        isEmpty: Boolean,
        mainDocumentPart: MainDocumentPart,
        functions: Array<(
            documentId: String,
            rPr: RPr,
            p: Int,
            r: Int,
            isEmpty: Boolean,
            mainDocumentPart: MainDocumentPart
        ) -> DocumentError?>
    ) {
        for (func in functions) {
            val error = func(document.id, rPr, p, r, isEmpty, mainDocumentPart)
            if (error != null) {
                errors += error
            }
        }
    }

    open fun findBasePRErrors(
        p: Int,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart,
        pFunctions: Array<(
            documentId: String,
            p: Int,
            isEmpty: Boolean,
            pPr: PPr,
            mainDocumentPart: MainDocumentPart
        ) -> DocumentError?>,
        rFunctions: Array<(
            documentId: String,
            rPr: RPr,
            p: Int,
            r: Int,
            isEmpty: Boolean,
            mainDocumentPart: MainDocumentPart
        ) -> DocumentError?>,
        commonPErrorFinderLauncher: (
            pPr: PPr,
            p: Int,
            isEmpty: Boolean,
            mainDocumentPart: MainDocumentPart,
            Array<(
                documentId: String,
                p: Int,
                isEmpty: Boolean,
                pPr: PPr,
                mainDocumentPart: MainDocumentPart
            ) -> DocumentError?>
        ) -> Unit = this::launchBasePErrorFinder,
        commonRErrorFinderLauncher: (
            rPr: RPr,
            p: Int,
            r: Int,
            isEmpty: Boolean,
            mainDocumentPart: MainDocumentPart,
            Array<(
                documentId: String,
                rPr: RPr,
                p: Int,
                r: Int,
                isEmpty: Boolean,
                mainDocumentPart: MainDocumentPart
            ) -> DocumentError?>
        ) -> Unit = this::launchBaseRErrorFinder
    ) {
        val paragraph = mainDocumentPart.content[p] as P
        val text = TextUtils.getText(p)
        val isEmpty = text?.isEmpty() ?: false
        commonPErrorFinderLauncher(pPr, p, isEmpty, mainDocumentPart, pFunctions)
        for (run in 0 until paragraph.content.size) {
            if (paragraph.content[run] is R) {
                val rPr = resolver.getEffectiveRPr((paragraph.content[run] as R).rPr, paragraph.pPr)
                commonRErrorFinderLauncher(rPr, p, run, isEmpty, mainDocumentPart, rFunctions)
            }
        }
    }

    fun validateList(startParagraph: Int) {
        var end = startParagraph
        val list = ArrayList<P>()
        while (mainDocumentPart.content.size > end
            && mainDocumentPart.content[end] is P
            && (mainDocumentPart.content[end] as P).pPr.numPr != null
        ) {
            list += mainDocumentPart.content[end] as P
            end++
        }
    }
}