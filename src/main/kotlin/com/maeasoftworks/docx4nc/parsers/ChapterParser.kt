package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.*
import com.maeasoftworks.docx4nc.enums.MistakeType.*
import com.maeasoftworks.docx4nc.model.Chapter
import com.maeasoftworks.docx4nc.model.Picture
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
        root.addMistake(DOCUMENT_UNEXPECTED_CONTENT, p, r)
    }

    open fun handleTable(p: Int) {
        root.addMistake(DOCUMENT_UNEXPECTED_CONTENT, p)
    }

    open fun handleContents(p: Int) {
        root.addMistake(DOCUMENT_UNEXPECTED_CONTENT, p)
    }

    fun parse(
        context: ChapterParser,
        headerPFunctions: Iterable<PFunction>?,
        headerRFunctions: Iterable<RFunction>?,
        pFunctions: Iterable<PFunction>,
        rFunctions: Iterable<RFunction>
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

    private fun handleContent(
        p: Int,
        context: ChapterParser,
        pFunctions: Iterable<PFunction>,
        rFunctions: Iterable<RFunction>
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
            else -> root.addMistake(DOCUMENT_UNEXPECTED_CONTENT, p, description = something::class.simpleName!!)
        }
    }

    open fun handleP(
        context: ChapterParser,
        p: Int,
        paragraph: P,
        pFunctions: Iterable<PFunction>,
        rFunctions: Iterable<RFunction>
    ) {
        val pPr = root.resolver.getEffectivePPr(paragraph.pPr)
        val isEmpty = TextUtils.getText(paragraph).isBlank()
        for (r in 0 until paragraph.content.size) {
            context.parseR(p, r, paragraph, rFunctions)
        }
        context.parseP(p, pPr, isEmpty, pFunctions)
    }

    open fun parseP(p: Int, pPr: PPr, isEmpty: Boolean, pFunctions: Iterable<PFunction>) {
        pFunctions.apply(root, p, pPr, isEmpty)
        if (pPr.numPr != null && pPr.numPr.numId.`val`.toInt() != 0) {
            validateListElement(p)
        } else {
            listPosition = 0
        }
    }

    open fun parseR(p: Int, r: Int, paragraph: P, rFunctions: Iterable<RFunction>) {
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
        root.addMistake(PARAGRAPH_UNEXPECTED_CONTENT, p, description = something::class.simpleName!!)
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
                    root.addMistake(WORD_GRAMMATICAL_ERROR, p, r + 1)
                } else if (something.type == "spellStart") {
                    root.addMistake(WORD_SPELL_ERROR, p, r + 1)
                }
            }
            is Br,
            is RunIns,
            is RunDel -> unexpectedP(p, something)

            /*
            * CommentRangeStart
            * CommentRangeEnd
            * */
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
                is R.DayLong -> root.addMistake(RUN_UNEXPECTED_CONTENT, p, r, something.declaredType.simpleName)
                /*
                * R.LastRenderedPageBreak
                * */
            }
            is Br -> root.addMistake(TODO_ERROR, p, r)
            is DelText -> root.addMistake(TODO_ERROR, p, r)
            is AlternateContent -> {
                // todo: is it only first object?
                if (something.choice.first().any.first() is Drawing) {
                    pictureTitleExpected = true
                    context.handleDrawing(p, r, c, root.pictures)
                } else {
                    root.addMistake(TODO_ERROR, p, r)
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
            root.addMistake(PICTURE_IS_NOT_INLINED, p, r)
        } else {
            container.add(picture)
            picture.title = TextUtils.getText(root.mainDocumentPart.content[p + 1] as P).let {
                if (it.uppercase().startsWith("РИСУНОК ")) {
                    return@let it
                } else {
                    root.addMistake(PICTURE_TITLE_REQUIRED_LINE_BREAK_BETWEEN_PICTURE_AND_TITLE, p, r)
                    return@let null
                }
            }
            if (TextUtils.getText(root.mainDocumentPart.content[p - 1] as P).isNotBlank()) {
                root.addMistake(PICTURE_REQUIRED_BLANK_LINE_BEFORE_PICTURE, p, r)
            }
            if (!root.isHeader(p + 2)) {
                if (TextUtils.getText(root.mainDocumentPart.content[p + 2] as P).isNotBlank()) {
                    root.addMistake(PICTURE_REQUIRED_BLANK_LINE_AFTER_PICTURE_TITLE, p, r)
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
            root.addMistake(LIST_LEVEL_MORE_THAN_2, p)
        }
        if (pPr.numPr.ilvl.`val`.toInt() == 0) {
            when (numberingFormat.listLevels["0"]!!.numFmt) {
                NumberFormat.BULLET -> if (numberingFormat.listLevels["0"]!!.levelText != "–") {
                    root.addMistake(ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_1, p, description = "/\"–\" (U+2013)")
                }
                NumberFormat.RUSSIAN_LOWER -> if (numberingFormat.listLevels["0"]!!.levelText != "%1)") {
                    root.addMistake(
                        ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_1,
                        p,
                        description = "/\"<RU_LOWER_LETTER>)\""
                    )
                }
                else -> root.addMistake(ORDERED_LIST_INCORRECT_MARKER_FORMAT, p)
            }
        } else if (pPr.numPr.ilvl.`val`.toInt() == 1) {
            if (numberingFormat.listLevels["1"]!!.numFmt != NumberFormat.DECIMAL || numberingFormat.listLevels["1"]!!.levelText != "%2)") {
                root.addMistake(ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_2, p, description = "/\"<DIGIT>)\"")
            }
        }
    }

    companion object {

        val pCommonFunctions = listOf(
            Rules.Default.Common.P::hasNotBackground,
            Rules.Default.Common.P::notBordered
        )

        val rCommonFunctions = listOf(
            Rules.Default.Common.R::isTimesNewRoman,
            Rules.Default.Common.R::fontSizeIs14,
            Rules.Default.Common.R::notItalic,
            Rules.Default.Common.R::notCrossedOut,
            Rules.Default.Common.R::notHighlighted,
            Rules.Default.Common.R::isBlack,
            Rules.Default.Common.R::letterSpacingIs0
        )

        val headerRFunctions = listOf(
            Rules.Default.Header.R::isBold,
            Rules.Default.Header.R::isUppercase
        )

        val headerPFunctions = listOf(
            Rules.Default.Header.P::justifyIsCenter,
            Rules.Default.Header.P::lineSpacingIsOne,
            Rules.Default.Header.P::emptyLineAfterHeaderExists,
            Rules.Default.Header.P::hasNotDotInEnd
        )

        val regularPFunctions = listOf(
            Rules.Default.RegularText.P::leftIndentIs0,
            Rules.Default.RegularText.P::rightIndentIs0,
            Rules.Default.RegularText.P::firstLineIndentIs1dot25,
            Rules.Default.RegularText.P::justifyIsBoth,
            Rules.Default.RegularText.P::lineSpacingIsOneAndHalf
        )

        val regularRFunctions = listOf(
            Rules.Default.RegularText.R::isNotBold,
            Rules.Default.RegularText.R::isNotCaps,
            Rules.Default.RegularText.R::isUnderline
        )
    }
}
