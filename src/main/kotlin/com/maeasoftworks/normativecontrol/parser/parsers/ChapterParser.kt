package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.entities.Mistake
import com.maeasoftworks.normativecontrol.parser.PFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.RFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.Rules
import com.maeasoftworks.normativecontrol.parser.apply
import com.maeasoftworks.normativecontrol.parser.enums.MistakeType.*
import com.maeasoftworks.normativecontrol.parser.model.Chapter
import com.maeasoftworks.normativecontrol.parser.model.Picture
import org.docx4j.TextUtils
import org.docx4j.dml.wordprocessingDrawing.Anchor
import org.docx4j.math.CTOMath
import org.docx4j.math.CTOMathPara
import org.docx4j.mce.AlternateContent
import org.docx4j.wml.*
import javax.xml.bind.JAXBElement

abstract class ChapterParser(protected val chapter: Chapter, val root: DocumentParser) {
    var pictureTitleExpected: Boolean = false
    val orderedListMarkers = "абвгежиклмнпрстуфхцшщэюя".toList()
    val alphabet = "абвгдежзиклмнопрстуфхцчшщыэюя".toList()
    private var listPosition = 0

    abstract fun parse()

    open fun handleHyperlink(p: Int, r: Int) {
        root.errors += Mistake(root.document.id, p, DOCUMENT_UNEXPECTED_CONTENT)
    }

    open fun handleTable(p: Int) {
        root.errors += Mistake(root.document.id, p, DOCUMENT_UNEXPECTED_CONTENT)
    }

    open fun handleContents(p: Int) {
        root.errors += Mistake(root.document.id, p, DOCUMENT_UNEXPECTED_CONTENT)
    }

    fun parse(
        context: ChapterParser,
        headerPFunctions: Iterable<PFunctionWrapper>?,
        headerRFunctions: Iterable<RFunctionWrapper>?,
        pFunctions: Iterable<PFunctionWrapper>,
        rFunctions: Iterable<RFunctionWrapper>
    ) {
        if (headerPFunctions != null && headerRFunctions != null) {
            handleP(
                this,
                chapter.startPos,
                root.mainDocumentPart.content[chapter.startPos] as P,
                headerPFunctions,
                headerRFunctions
            )
        }
        for (p in chapter.startPos + 1 until chapter.startPos + chapter.content.size) {
            handleContent(p, context, pFunctions, rFunctions)
        }
    }

    fun handleContent(
        p: Int,
        context: ChapterParser,
        pFunctions: Iterable<PFunctionWrapper>,
        rFunctions: Iterable<RFunctionWrapper>
    ) {
        when (val something = root.mainDocumentPart.content[p]) {
            is P -> {
                handleP(context, p, something, pFunctions, rFunctions)
            }
            is SdtBlock -> {
                context.handleContents(p)
            }
            is Tbl -> {
                context.handleTable(p)
            }
            else -> root.errors += Mistake(
                root.document.id,
                p,
                DOCUMENT_UNEXPECTED_CONTENT,
                something::class.simpleName!!
            )
        }
    }

    open fun handleP(
        context: ChapterParser,
        p: Int,
        paragraph: P,
        pFunctions: Iterable<PFunctionWrapper>,
        rFunctions: Iterable<RFunctionWrapper>
    ) {
        val pPr = root.resolver.getEffectivePPr(paragraph.pPr)
        val isEmpty = TextUtils.getText(paragraph).isBlank()
        for (r in 0 until paragraph.content.size) {
            context.parseR(p, r, paragraph, rFunctions)
        }
        context.parseP(p, pPr, isEmpty, pFunctions)
    }

    open fun parseP(p: Int, pPr: PPr, isEmpty: Boolean, pFunctions: Iterable<PFunctionWrapper>) {
        pFunctions.apply(root, p, pPr, isEmpty)
        if (pPr.numPr != null && pPr.numPr.numId.`val`.toInt() != 0) {
            validateListElement(p)
        } else {
            listPosition = 0
        }
    }

    open fun parseR(p: Int, r: Int, paragraph: P, rFunctions: Iterable<RFunctionWrapper>) {
        if (paragraph.content[r] is R) {
            rFunctions.apply(
                root,
                p,
                r,
                root.resolver.getEffectiveRPr((paragraph.content[r] as R).rPr, paragraph.pPr),
                TextUtils.getText(paragraph.content[r]).isBlank()
            )
        } else {
            handlePContent(p, r, this)
        }
    }

    private fun unexpectedP(p: Int, something: Any) {
        root.errors += Mistake(root.document.id, p, PARAGRAPH_UNEXPECTED_CONTENT, something::class.simpleName!!)
    }

    open fun handlePContent(p: Int, r: Int, context: ChapterParser) {
        when (val something = (root.mainDocumentPart.content[p] as P).content[r]) {
            is JAXBElement<*> -> when (something.value) {
                is P.Hyperlink -> context.handleHyperlink(p, r)
                is CTRel -> unexpectedP(p, something)
                is CTMarkup -> when (something.value) {
                    is CTMarkupRange -> when (something.value) {
                        is CTBookmark -> when (something.value) {
                            is CTMoveBookmark -> unexpectedP(p, something)
                        }
                    }
                    is CTMoveToRangeEnd -> unexpectedP(p, something)
                    is CTTrackChange -> when (something.value) {
                        is RunTrackChange -> unexpectedP(p, something)
                    }
                    is CTMoveFromRangeEnd -> unexpectedP(p, something)
                }
                is ContentAccessor -> when (something.value) {
                    is CTSimpleField,
                    is CTSmartTagRun,
                    is CTCustomXmlRun -> unexpectedP(p, something)
                }
                is CTPerm -> when (something.value) {
                    is RangePermissionStart -> unexpectedP(p, something)
                }
                is CTOMathPara,
                is CTOMath,
                is SdtRun,
                is P.Bdo,
                is P.Dir -> unexpectedP(p, something)
            }
            is ProofErr -> {
                if (something.type == "gramStart") {
                    root.errors += Mistake(root.document.id, p, r + 1, WORD_GRAMMATICAL_ERROR)
                } else if (something.type == "spellStart") {
                    root.errors += Mistake(root.document.id, p, r + 1, WORD_SPELL_ERROR)
                }
            }
            is Br,
            is RunIns,
            is RunDel,
            is CommentRangeStart,
            is CommentRangeEnd -> unexpectedP(p, something)
        }
    }

    open fun handleRContent(p: Int, r: Int, c: Int, context: ChapterParser, pictureContainer: MutableList<Picture>) {
        when (val something = ((root.mainDocumentPart.content[p] as P).content[r] as R).content[c]) {
            is JAXBElement<*> -> when (something.value) {
                is Drawing -> {
                    pictureTitleExpected = true
                    context.handleDrawing(p, r, c, pictureContainer)
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
                is R.DayLong -> root.errors += Mistake(
                    root.document.id,
                    p,
                    r,
                    RUN_UNEXPECTED_CONTENT,
                    something.declaredType.simpleName
                )
            }
            is Br -> root.errors += Mistake(root.document.id, p, r, TODO_ERROR)
            is DelText -> root.errors += Mistake(root.document.id, p, r, TODO_ERROR)
            is AlternateContent -> {
                // todo: is it only first object?
                if (something.choice.first().any.first() is Drawing) {
                    pictureTitleExpected = true
                    context.handleDrawing(p, r, c, root.pictures)
                } else {
                    root.errors += Mistake(root.document.id, p, r, TODO_ERROR)
                }
            }
        }
    }

    open fun handleDrawing(p: Int, r: Int, c: Int, container: MutableList<Picture>) {
        val drawing: Drawing = try {
            (((root.mainDocumentPart.content[p] as P).content[r] as R).content[c] as JAXBElement<*>).value as Drawing
        } catch (e: java.lang.ClassCastException) {
            (((root.mainDocumentPart.content[p] as P).content[r] as R).content[c] as AlternateContent).choice.first().any.first() as Drawing
        }

        val picture = Picture(p, r, c, drawing)
        if ((drawing.anchorOrInline as ArrayListWml<*>)[0] is Anchor) {
            root.errors += Mistake(root.document.id, p, r, PICTURE_IS_NOT_INLINED)
        } else {
            container.add(picture)
            picture.title = TextUtils.getText(root.mainDocumentPart.content[p + 1] as P).let {
                if (it.uppercase().startsWith("РИСУНОК ")) {
                    it
                } else {
                    root.errors += Mistake(
                        root.document.id,
                        p,
                        r,
                        PICTURE_TITLE_REQUIRED_LINE_BREAK_BETWEEN_PICTURE_AND_TITLE
                    )
                    null
                }
            }
            if (TextUtils.getText(root.mainDocumentPart.content[p - 1] as P).isNotBlank()) {
                root.errors += Mistake(root.document.id, p, r, PICTURE_REQUIRED_BLANK_LINE_BEFORE_PICTURE)
            }
            if (!root.isHeader(p + 2)) {
                if (TextUtils.getText(root.mainDocumentPart.content[p + 2] as P).isNotBlank()) {
                    root.errors += Mistake(
                        root.document.id,
                        p,
                        r,
                        PICTURE_REQUIRED_BLANK_LINE_AFTER_PICTURE_TITLE
                    )
                }
            }
        }
    }

    open fun pictureTitleMatcher(title: String): MatchResult? {
        return Regex("РИСУНОК (\\d+)").find(title.uppercase())
    }

    fun validatePictureTitleStyle(pictureP: Int) {
        TitleParser(
            Chapter(pictureP + 1, listOf(root.mainDocumentPart.content[pictureP + 1]).toMutableList()),
            root
        ).parse()
    }

    fun validateListElement(p: Int) {
        val pPr = (root.mainDocumentPart.content[p] as P).pPr
        val numberingFormat =
            root.numbering!!.instanceListDefinitions[pPr.numPr.numId.`val`.toString()]!!.abstractListDefinition
        if (pPr.numPr.ilvl.`val` != null && pPr.numPr.ilvl.`val`.toInt() > 1) {
            root.addError(LIST_LEVEL_MORE_THAN_2, p)
        }
        if (pPr.numPr.ilvl.`val`.toInt() == 0) {
            when (numberingFormat.listLevels["0"]!!.numFmt) {
                NumberFormat.BULLET -> if (numberingFormat.listLevels["0"]!!.levelText != "–") {
                    root.addError(ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_1, p, description = "/\"–\" (U+2013)")
                }
                NumberFormat.RUSSIAN_LOWER -> if (numberingFormat.listLevels["0"]!!.levelText != "%1)") {
                    root.addError(
                        ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_1,
                        p,
                        description = "/\"<RU_LOWER_LETTER>)\""
                    )
                }
                else -> root.addError(ORDERED_LIST_INCORRECT_MARKER_FORMAT, p)
            }
        } else if (pPr.numPr.ilvl.`val`.toInt() == 1) {
            if (numberingFormat.listLevels["1"]!!.numFmt != NumberFormat.DECIMAL || numberingFormat.listLevels["1"]!!.levelText != "%2)") {
                root.addError(ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_2, p, description = "/\"<DIGIT>)\"")
            }
        }
    }

    companion object {

        val pCommonFunctions = PFunctionWrapper.iterable(
            Rules.Default.Common.P::hasNotBackground,
            Rules.Default.Common.P::notBordered
        )

        val rCommonFunctions = RFunctionWrapper.iterable(
            Rules.Default.Common.R::isTimesNewRoman,
            Rules.Default.Common.R::fontSizeIs14,
            Rules.Default.Common.R::notItalic,
            Rules.Default.Common.R::notCrossedOut,
            Rules.Default.Common.R::notHighlighted,
            Rules.Default.Common.R::isBlack,
            Rules.Default.Common.R::letterSpacingIs0
        )

        val headerRFunctions = RFunctionWrapper.iterable(
            Rules.Default.Header.R::isBold,
            Rules.Default.Header.R::isUppercase
        )

        val headerPFunctions = PFunctionWrapper.iterable(
            Rules.Default.Header.P::justifyIsCenter,
            Rules.Default.Header.P::lineSpacingIsOne,
            Rules.Default.Header.P::emptyLineAfterHeaderExists,
            Rules.Default.Header.P::hasNotDotInEnd
        )

        val regularPFunctions = PFunctionWrapper.iterable(
            Rules.Default.RegularText.P::leftIndentIs0,
            Rules.Default.RegularText.P::rightIndentIs0,
            Rules.Default.RegularText.P::firstLineIndentIs1dot25,
            Rules.Default.RegularText.P::justifyIsBoth,
            Rules.Default.RegularText.P::lineSpacingIsOneAndHalf
        )

        val regularRFunctions = RFunctionWrapper.iterable(
            Rules.Default.RegularText.R::isNotBold,
            Rules.Default.RegularText.R::isNotCaps,
            Rules.Default.RegularText.R::isUnderline
        )
    }
}