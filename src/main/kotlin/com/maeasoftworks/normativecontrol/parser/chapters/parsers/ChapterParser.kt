package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.Document
import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import org.docx4j.TextUtils
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.P
import org.docx4j.wml.PPr
import org.docx4j.wml.R
import org.docx4j.wml.RPr

abstract class ChapterParser(
    override val document: Document,
    protected val chapter: Chapter
) : DocumentParser(document) {
    constructor(parser: DocumentParser, chapter: Chapter) : this(parser.document, chapter) {
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
        pFunctionWrappers: Iterable<PFunctionWrapper>
    ) {
        for (wrapper in pFunctionWrappers) {
            val error = wrapper.function(document.id, p, isEmpty, pPr, mainDocumentPart)
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
        rFunctionWrappers: Iterable<RFunctionWrapper>
    ) {
        for (wrapper in rFunctionWrappers) {
            val error = wrapper.function(document.id, rPr, p, r, isEmpty, mainDocumentPart)
            if (error != null) {
                errors += error
            }
        }
    }

    open fun findBasePRErrors(
        p: Int,
        pPr: PPr,
        mainDocumentPart: MainDocumentPart,
        pFunctionWrappers: Iterable<PFunctionWrapper>,
        rFunctionWrappers: Iterable<RFunctionWrapper>,
        commonPErrorFinderLauncher: (PPr, Int, Boolean, MainDocumentPart, Iterable<PFunctionWrapper>) -> Unit = this::launchBasePErrorFinder,
        commonRErrorFinderLauncher: (RPr, Int, Int, Boolean, MainDocumentPart, Iterable<RFunctionWrapper>) -> Unit = this::launchBaseRErrorFinder,
        //customRTypeHandler: (Int, Int, MainDocumentPart) -> Unit
    ) {
        val paragraph = mainDocumentPart.content[p] as P
        val text = TextUtils.getText(p)
        val isEmpty = text?.isEmpty() ?: false
        commonPErrorFinderLauncher(pPr, p, isEmpty, mainDocumentPart, pFunctionWrappers)
        for (r in 0 until paragraph.content.size) {
            if (paragraph.content[r] is R) {
                val rPr = resolver.getEffectiveRPr((paragraph.content[r] as R).rPr, paragraph.pPr)
                commonRErrorFinderLauncher(rPr, p, r, isEmpty, mainDocumentPart, rFunctionWrappers)
            } /*else {
                customRTypeHandler(p, r, mainDocumentPart)
            }*/
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

    companion object {
        fun createRRulesCollection(vararg rules: (String, RPr, Int, Int, Boolean, MainDocumentPart) -> DocumentError?): Iterable<RFunctionWrapper> =
            rules.map { RFunctionWrapper(it) }

        fun createPRulesCollection(vararg rules: (String, Int, Boolean, PPr, MainDocumentPart) -> DocumentError?): Iterable<PFunctionWrapper> =
            rules.map { PFunctionWrapper(it) }
    }
}