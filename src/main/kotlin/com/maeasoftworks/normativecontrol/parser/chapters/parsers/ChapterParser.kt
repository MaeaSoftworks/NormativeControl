package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.Document
import com.maeasoftworks.normativecontrol.parser.DocumentParser
import com.maeasoftworks.normativecontrol.parser.chapters.Chapter
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType
import org.docx4j.TextUtils
import org.docx4j.math.CTOMath
import org.docx4j.math.CTOMathPara
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import javax.xml.bind.JAXBElement

abstract class ChapterParser(document: Document, protected val chapter: Chapter) : DocumentParser(document) {
    constructor(parser: DocumentParser, chapter: Chapter) : this(parser.document, chapter) {
        mlPackage = parser.mlPackage
        mainDocumentPart = parser.mainDocumentPart
        resolver = parser.resolver
        parsers = parser.parsers
        errors = parser.errors
        tables = parser.tables
        pictures = parser.pictures
    }

    open fun parse(parser: ChapterParser = this) {
        parser.parseHeader()
        for (p in chapter.startPos + 1 until chapter.startPos + chapter.content.size) {
            val pPr = resolver.getEffectivePPr((mainDocumentPart.content[p] as P).pPr)
            val paragraph = mainDocumentPart.content[p] as P
            val isEmptyP = TextUtils.getText(paragraph).isEmpty()
            parser.parseP(p, pPr, isEmptyP)
            for (r in 0 until paragraph.content.size) {
                parser.parseR(p, r, paragraph)
            }
        }
    }

    abstract fun parseHeader()

    abstract fun parseP(p: Int, pPr: PPr, isEmpty: Boolean)

    abstract fun parseR(p: Int, r: Int, paragraph: P)

    abstract fun applyPFunctions(p: Int, pPr: PPr, isEmpty: Boolean, pFunctionWrappers: Iterable<PFunctionWrapper>)

    abstract fun applyRFunctions(p: Int, r: Int, rPr: RPr, isEmpty: Boolean, rFunctionWrappers: Iterable<RFunctionWrapper>)

    open fun handlePContent(p: Int, r: Int, parser: ChapterParser) {
        when (val something = (mainDocumentPart.content[p] as P).content[r]) {
            is JAXBElement<*> -> when (something.value) {
                is P.Hyperlink -> parser.handleHyperlink(p, r)
                is CTRel -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                is CTMarkup -> when (something.value) {
                    is CTMarkupRange -> when (something.value) {
                        is CTBookmark -> when (something.value) {
                            is CTMoveBookmark -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                        }
                    }
                    is CTMoveToRangeEnd -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                    is CTTrackChange -> when (something.value) {
                        is RunTrackChange -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                    }
                    is CTMoveFromRangeEnd -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                }
                is ContentAccessor -> when (something.value) {
                    is CTSimpleField -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                    is CTSmartTagRun -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                    is CTCustomXmlRun -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                }
                is CTPerm -> when (something.value) {
                    is RangePermissionStart -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                }
                is CTOMathPara -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                is CTOMath -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                is SdtRun -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                is P.Bdo -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
                is P.Dir -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
            }
            is ProofErr -> {
                if (something.type == "gramStart") {
                    errors += DocumentError(document.id, p, r + 1, ErrorType.WORD_GRAMMATICAL_ERROR)
                } else if (something.type == "spellStart") {
                    errors += DocumentError(document.id, p, r + 1, ErrorType.WORD_SPELL_ERROR)
                }
            }
            is Br -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
            is RunIns -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
            is RunDel -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
            is CommentRangeStart -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
            is CommentRangeEnd -> errors += DocumentError(document.id, p, r, ErrorType.TODO_ERROR)
        }
    }

    abstract fun handleHyperlink(p: Int, r: Int)

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