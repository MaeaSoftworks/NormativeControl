package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.Document
import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.P
import org.docx4j.wml.PPr
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

    abstract fun findPErrors(p: Int, pPr: PPr, isEmpty: Boolean, pFunctionWrappers: Iterable<PFunctionWrapper>)

    abstract fun findRErrors(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, rFunctionWrappers: Iterable<RFunctionWrapper>)

    abstract fun handleNotRContent(p: Int, r: Int)

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
        fun createRRulesCollection(vararg rules: (String, Int, Int, RPr, Boolean, MainDocumentPart) -> DocumentError?): List<RFunctionWrapper> =
            rules.map { RFunctionWrapper(it) }

        fun createPRulesCollection(vararg rules: (String, Int, PPr, Boolean, MainDocumentPart) -> DocumentError?): List<PFunctionWrapper> =
            rules.map { PFunctionWrapper(it) }
    }
}