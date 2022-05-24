package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.PFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.RFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.model.Chapter
import com.maeasoftworks.normativecontrol.parser.model.Picture
import com.maeasoftworks.normativecontrol.parser.enums.ErrorType.*
import com.maeasoftworks.normativecontrol.utils.smartAdd
import org.docx4j.TextUtils
import org.docx4j.dml.wordprocessingDrawing.Anchor
import org.docx4j.math.CTOMath
import org.docx4j.math.CTOMathPara
import org.docx4j.mce.AlternateContent
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import javax.xml.bind.JAXBElement

abstract class ChapterParser(protected val chapter: Chapter, val root: DocumentParser) : DocumentParser(root.document) {
    var pictureTitleExpected: Boolean = false

    open fun parse(context: ChapterParser = this) {
        context.parseHeader()
        for (p in chapter.startPos + 1 until chapter.startPos + chapter.content.size) {
            handleContent(p, context)
        }
    }

    abstract fun parseHeader()

    abstract fun parseP(p: Int, pPr: PPr, isEmpty: Boolean)

    abstract fun parseR(p: Int, r: Int, paragraph: P)

    open fun applyPFunctions(p: Int, pPr: PPr, isEmpty: Boolean, pFunctionWrappers: Iterable<PFunctionWrapper>) {
        for (wrapper in pFunctionWrappers) {
            errors.smartAdd(wrapper.function(document.id, p, pPr, isEmpty, mainDocumentPart))
        }
    }

    open fun applyRFunctions(
        p: Int,
        r: Int,
        rPr: RPr,
        isEmpty: Boolean,
        rFunctionWrappers: Iterable<RFunctionWrapper>
    ) {
        for (wrapper in rFunctionWrappers) {
            errors.smartAdd(wrapper.function(document.id, p, r, rPr, isEmpty, mainDocumentPart))
        }
    }

    open fun handleContent(p: Int, context: ChapterParser) {
        when (val something = mainDocumentPart.content[p]) {
            is P -> {
                val pPr = resolver.getEffectivePPr(something.pPr)
                val paragraph = mainDocumentPart.content[p] as P
                val isEmptyP = TextUtils.getText(paragraph).isBlank()
                context.parseP(p, pPr, isEmptyP)
                for (r in 0 until paragraph.content.size) {
                    context.parseR(p, r, paragraph)
                }
            }
        }
    }

    open fun handlePContent(p: Int, r: Int, context: ChapterParser) {
        when (val something = (mainDocumentPart.content[p] as P).content[r]) {
            is JAXBElement<*> -> when (something.value) {
                is P.Hyperlink -> context.handleHyperlink(p, r)
                is CTRel -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                is CTMarkup -> when (something.value) {
                    is CTMarkupRange -> when (something.value) {
                        is CTBookmark -> when (something.value) {
                            is CTMoveBookmark -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                        }
                    }
                    is CTMoveToRangeEnd -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                    is CTTrackChange -> when (something.value) {
                        is RunTrackChange -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                    }
                    is CTMoveFromRangeEnd -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                }
                is ContentAccessor -> when (something.value) {
                    is CTSimpleField -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                    is CTSmartTagRun -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                    is CTCustomXmlRun -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                }
                is CTPerm -> when (something.value) {
                    is RangePermissionStart -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                }
                is CTOMathPara -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                is CTOMath -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                is SdtRun -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                is P.Bdo -> errors += DocumentError(document.id, p, r, TODO_ERROR)
                is P.Dir -> errors += DocumentError(document.id, p, r, TODO_ERROR)
            }
            is ProofErr -> {
                if (something.type == "gramStart") {
                    errors += DocumentError(document.id, p, r + 1, WORD_GRAMMATICAL_ERROR)
                } else if (something.type == "spellStart") {
                    errors += DocumentError(document.id, p, r + 1, WORD_SPELL_ERROR)
                }
            }
            is Br -> errors += DocumentError(document.id, p, r, TODO_ERROR)
            is RunIns -> errors += DocumentError(document.id, p, r, TODO_ERROR)
            is RunDel -> errors += DocumentError(document.id, p, r, TODO_ERROR)
            is CommentRangeStart -> errors += DocumentError(document.id, p, r, TODO_ERROR)
            is CommentRangeEnd -> errors += DocumentError(document.id, p, r, TODO_ERROR)
        }
    }

    open fun handleRContent(p: Int, r: Int, c: Int, context: ChapterParser, container: MutableList<Picture>) {
        when (val something = ((mainDocumentPart.content[p] as P).content[r] as R).content[c]) {
            is JAXBElement<*> -> when (something.value) {
                is Drawing -> {
                    pictureTitleExpected = true
                    context.handleDrawing(p, r, c, container)
                }
                is Text -> Unit
                is R.Ptab,
                is R.YearLong,
                is R.DayShort,
                is R.NoBreakHyphen,
                is R.EndnoteRef,
                is R.PgNum,
                is R.SoftHyphen,
                is Pict,
                is R.Tab,
                is R.Separator,
                is R.LastRenderedPageBreak,
                is CTObject,
                is CTFtnEdnRef,
                is R.MonthShort,
                is R.MonthLong,
                is R.ContinuationSeparator,
                is CTRuby,
                is R.AnnotationRef,
                is R.Cr,
                is R.YearShort,
                is FldChar,
                is R.Sym,
                is R.CommentReference,
                is R.FootnoteRef,
                is R.DayLong -> errors += DocumentError(
                    document.id,
                    p,
                    r,
                    TODO_ERROR,
                    something.declaredType.simpleName
                )
            }
            is Br -> errors += DocumentError(document.id, p, r, TODO_ERROR)
            is DelText -> errors += DocumentError(document.id, p, r, TODO_ERROR)
            is AlternateContent -> {
                // todo: is it only first object?
                if (something.choice.first().any.first() is Drawing) {
                    pictureTitleExpected = true
                    context.handleDrawing(p, r, c, root.pictures)
                } else {
                    errors += DocumentError(document.id, p, r, TODO_ERROR)
                }
            }
        }
    }

    abstract fun handleHyperlink(p: Int, r: Int)

    open fun handleDrawing(p: Int, r: Int, c: Int, container: MutableList<Picture>) {
        val drawing: Drawing = try {
            (((mainDocumentPart.content[p] as P).content[r] as R).content[c] as JAXBElement<*>).value as Drawing
        } catch (e: java.lang.ClassCastException) {
            (((mainDocumentPart.content[p] as P).content[r] as R).content[c] as AlternateContent).choice.first().any.first() as Drawing
        }

        val picture = Picture(p, r, c, drawing)
        if ((drawing.anchorOrInline as ArrayListWml<*>)[0] is Anchor) {
            errors += DocumentError(document.id, p, r, PICTURE_IS_NOT_INLINED)
        } else {
            container.add(picture)
            picture.title = TextUtils.getText(mainDocumentPart.content[p + 1] as P).let {
                if (it.uppercase().startsWith("РИСУНОК ")) {
                    it
                } else {
                    errors += DocumentError(
                        document.id,
                        p,
                        r,
                        PICTURE_TITLE_REQUIRED_LINE_BREAK_BETWEEN_PICTURE_AND_TITLE
                    )
                    null
                }
            }
            if (TextUtils.getText(mainDocumentPart.content[p - 1] as P).isNotBlank()) {
                errors += DocumentError(document.id, p, r, PICTURE_REQUIRED_BLANK_LINE_BEFORE_PICTURE)
            }
            if (!isHeader(p + 2)) {
                if (TextUtils.getText(mainDocumentPart.content[p + 2] as P).isNotBlank()) {
                    errors += DocumentError(document.id, p, r, PICTURE_REQUIRED_BLANK_LINE_AFTER_PICTURE_TITLE)
                }
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

    companion object {
        fun createRRulesCollection(vararg rules: (String, Int, Int, RPr, Boolean, MainDocumentPart) -> DocumentError?): List<RFunctionWrapper> =
            rules.map { RFunctionWrapper(it) }

        fun createPRulesCollection(vararg rules: (String, Int, PPr, Boolean, MainDocumentPart) -> DocumentError?): List<PFunctionWrapper> =
            rules.map { PFunctionWrapper(it) }
    }

    init {
        mainDocumentPart = root.mainDocumentPart
        resolver = root.resolver
        parsers = root.parsers
        errors = root.errors
        tables = root.tables
    }
}