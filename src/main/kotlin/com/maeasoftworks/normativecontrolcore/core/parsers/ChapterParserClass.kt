package com.maeasoftworks.normativecontrolcore.core.parsers

/*
@Deprecated("Use ChapterParser")
/**
 * Abstract class for chapters' parsers.
 *
 * Default workflow:
 * - `parse()` called externally;
 * - `parse()` calls `parse(ChapterParser, PFunctions?, RFunctions?, PFunctions, RFunctions?)` with necessary
 * lists of rules;
 * - `parse(...)` iterates in paragraphs & runs:
 *     - starts style checking on simple paragraphs & runs;
 *     - calls handlers for other content types.
 *
 * How to use:
 * - inherit from this class;
 * - override `parse()` to `parse(ChapterParser, PFunctions?, RFunctions?, PFunctions, RFunctions?)` call with necessary
 * lists of rules or any other logic if necessary;
 * - if you use default `parse(...)`, override opened `handle*(...)` functions, otherwise, create your custom logic.
 * @param chapter chapter that will be parsed
 * @param root document parser
 */
abstract class ChapterParserClass(val chapter: Chapter, val root: DocumentParser) {
    // todo: maybe it's possible to create special class for it?
    var pictureTitleExpected = false
    protected var currentListStartValue = -1
    protected var listPosition = 0

    /**
     * Entry point for parser, will be called externally
     */
    abstract fun parse()

    //region document

    /**
     * Internal entry point for parser.
     *
     * By default, this function is called from entry point and starts iteration in chapter's paragraphs.
     * @param context instance of parser in which this function is called (used to call handlers)
     * @param headerPFunctions rules for header paragraph
     * @param headerRFunctions rules for header run
     * @param pFunctions rules for paragraphs in chapter
     * @param rFunctions rules for runs in chapter
     */
    fun parse(
        context: ChapterParserClass,
        headerPFunctions: PFunctions?,
        headerRFunctions: RFunctions?,
        pFunctions: PFunctions,
        rFunctions: RFunctions?
    ) {
        if (headerPFunctions != null && headerRFunctions != null) {
            handleP(
                context,
                chapter.startPos,
                root.doc.content[chapter.startPos] as P,
                headerPFunctions,
                headerRFunctions
            )
        }
        for (p in chapter.startPos + 1 until chapter.startPos + chapter.content.size) {
            handleDocumentContent(p, context, pFunctions, rFunctions)
        }
    }

    /**
     * Detects document content type
     * @param p index on p-layer
     * @param context instance of parser in which this function is called (used to call handlers)
     * @param pFunctions rules for paragraphs in chapter
     * @param rFunctions rules for runs in chapter
     */
    private fun handleDocumentContent(p: Int, context: ChapterParserClass, pFunctions: PFunctions, rFunctions: RFunctions?) {
        when (val something = root.doc.content[p]) {
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

    //endregion document

    //region paragraphs

    /**
     * Starts iteration in paragraph
     * @param context instance of parser in which this function is called (used to call handlers)
     * @param pPos index on p-layer
     * @param p paragraph instance
     * @param pFunctions rules for paragraphs in chapter
     * @param rFunctions rules for runs in chapter
     */
    open fun handleP(context: ChapterParserClass, pPos: Int, p: P, pFunctions: PFunctions, rFunctions: RFunctions?) {
        val isEmpty = root.texts.getText(p).isBlank()
        for (r in 0 until p.content.size) {
            if (rFunctions != null) {
                context.parseR(pPos, r, p, rFunctions)
            }
        }
        context.parseP(pPos, p, isEmpty, pFunctions)
    }

    /**
     * Starts style checking for paragraph
     * @param pPos index on p-layer
     * @param p paragraph
     * @param isEmpty indicates whether the text is empty
     * @param pFunctions rules for paragraphs in chapter
     */
    open fun parseP(pPos: Int, p: P, isEmpty: Boolean, pFunctions: PFunctions) {
        pFunctions.apply(root, pPos, p, isEmpty)
        val numPr = root.resolver.getActualProperty(p) { numPr }
        if (numPr != null && numPr.numId.`val`.toInt() != 0) {
            validateListElement(pPos, p)
        } else {
            listPosition = 0
            currentListStartValue = -1
        }
    }

    /**
     * Detects paragraph content type
     * @param p index on p-layer
     * @param r index on r-layer
     * @param context instance of parser in which this function is called (used to call handlers)
     */
    open fun handlePContent(p: Int, r: Int, context: ChapterParserClass) {
        when (val something = (root.doc.content[p] as P).content[r]) {
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
             * Detected:
             *   CommentRangeStart
             *   CommentRangeEnd
             */
        }
    }

    //endregion paragraphs

    //region runs

    /**
     * Starts style checking for paragraph
     * @param pPos index on p-layer
     * @param rPos index on r-layer
     * @param p paragraph instance
     * @param rFunctions rules for runs in chapter
     */
    open fun parseR(pPos: Int, rPos: Int, p: P, rFunctions: RFunctions) {
        val r = p.content[rPos]
        if (r is R) {
            rFunctions.apply(root, pPos, rPos, r, TextUtils.getText(r).isBlank())
        } else {
            handlePContent(pPos, rPos, this)
        }
    }

    /**
     * Detects paragraph content type
     * @param p index on p-layer
     * @param r index on r-layer
     * @param c index on c-layer
     * @param context instance of parser in which this function is called (used to call handlers)
     * @param pictureContainer list that contains pictures
     */
    open fun handleRContent(p: Int, r: Int, c: Int, context: ChapterParserClass, pictureContainer: MutableList<Picture>) {
        when (val something = ((root.doc.content[p] as P).content[r] as R).content[c]) {
            is JAXBElement<*> -> when (something.value) {
                is Drawing -> {
                    pictureTitleExpected = true
                    context.handleDrawing(p, r, c, pictureContainer)
                }

                is R.Tab -> {
                    if (c == 0) {
                        root.addMistake(TEXT_COMMON_USE_FIRST_LINE_INDENT_INSTEAD_OF_TAB, p, r)
                    }
                }

                is R.Ptab,
                is R.YearLong,
                is R.DayShort,
                is R.NoBreakHyphen,
                is R.EndnoteRef,
                is R.PgNum,
                is R.SoftHyphen,
                is Pict,
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
                 * Detected:
                 *   R.LastRenderedPageBreak
                 *   Text
                 */
            }

            is DelText -> root.addMistake(RUN_UNEXPECTED_CONTENT, p, r, something::class.simpleName)
            is AlternateContent -> {
                // todo: is it only first object?
                if (something.choice.first().any.first() is Drawing) {
                    pictureTitleExpected = true
                    context.handleDrawing(p, r, c, root.pictures)
                } else {
                    root.addMistake(RUN_UNEXPECTED_CONTENT, p, r, something::class.simpleName)
                }
            }
            /*
             * Detected:
             *   Br
             */
        }
    }

    //endregion runs

    //region handlers

    open fun handleHyperlink(p: Int, r: Int) {
        root.addMistake(DOCUMENT_UNEXPECTED_CONTENT, p, r)
    }

    open fun handleTable(p: Int) {
        root.addMistake(DOCUMENT_UNEXPECTED_CONTENT, p)
    }

    open fun handleContents(p: Int) {
        root.addMistake(DOCUMENT_UNEXPECTED_CONTENT, p)
    }

    open fun handleDrawing(p: Int, r: Int, c: Int, container: MutableList<Picture>) {
        val drawing: Drawing = try {
            (((root.doc.content[p] as P).content[r] as R).content[c] as JAXBElement<*>).value as Drawing
        } catch (e: java.lang.ClassCastException) {
            (((root.doc.content[p] as P).content[r] as R).content[c] as AlternateContent).choice.first().any.first() as Drawing
        }

        val picture = Picture(p, r, c, drawing)
        if ((drawing.anchorOrInline as ArrayListWml<*>)[0] is Anchor) {
            root.addMistake(PICTURE_IS_NOT_INLINED, p, r)
        } else {
            container.add(picture)
            picture.title = root.texts.getText(root.doc.content[p + 1] as P).let {
                if (it.uppercase().startsWith("РИСУНОК ")) {
                    return@let it
                } else {
                    root.addMistake(PICTURE_TITLE_REQUIRED_LINE_BREAK_BETWEEN_PICTURE_AND_TITLE, p, r)
                    return@let null
                }
            }
            if (root.texts.getText(root.doc.content[p - 1] as P).isNotBlank()) {
                root.addMistake(PICTURE_REQUIRED_BLANK_LINE_BEFORE_PICTURE, p, r)
            }
            if (!root.isHeaderOfLevel(p + 2)) {
                if (root.texts.getText(root.doc.content[p + 2] as P).isNotBlank()) {
                    root.addMistake(PICTURE_REQUIRED_BLANK_LINE_AFTER_PICTURE_TITLE, p, r)
                }
            }
        }
    }

    //endregion handlers

    //region pictures

    open fun pictureTitleMatcher(title: String): MatchResult? {
        return Regex("РИСУНОК (\\d+)").find(title.uppercase())
    }

    fun validatePictureTitleStyle(pictureP: Int) {
        PictureTitleParser(
            Chapter(pictureP + 1, mutableListOf(root.doc.content[pictureP + 1])),
            root
        ).parse()
    }

    //endregion pictures

    //region lists

    fun validateListElement(pPos: Int, p: P) {
        val numFormat = root.resolver.getActualProperty(p) { numPr }
        val numberingFormat = root.numbering!!.instanceListDefinitions[numFormat?.numId?.`val`.toString()]!!.abstractListDefinition
        if (numFormat?.ilvl?.`val` != null && numFormat.ilvl.`val`.toInt() > 1) {
            root.addMistake(LIST_LEVEL_MORE_THAN_2, pPos)
        }
        if (numFormat?.ilvl?.`val` != null && numFormat.ilvl.`val`.toInt() == 0) {
            // todo add support to custom multilevel lists
            when (numberingFormat.listLevels["0"]?.numFmt) {
                NumberFormat.BULLET -> if (numberingFormat.listLevels["0"]!!.levelText != "–") {
                    root.addMistake(ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_1, pPos, description = "\"–\" (U+2013)")
                }

                NumberFormat.RUSSIAN_LOWER -> if (numberingFormat.listLevels["0"]!!.levelText != "%1)") {
                    root.addMistake(ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_1, pPos, description = "\"<RU_LOWER_LETTER>)\"")
                } else {
                    if (numberingFormat.listLevels["0"]!!.startValue.toInt() == currentListStartValue) {
                        listPosition++
                    } else {
                        currentListStartValue = numberingFormat.listLevels["0"]!!.startValue.toInt()
                        listPosition = currentListStartValue
                    }
                    if (alphabet[listPosition] !in orderedListMarkers) {
                        root.addMistake(
                            ORDERED_LIST_WRONG_LETTER,
                            pPos,
                            description = "Запрещены: \"ё\", \"з\", \"й\", \"о\", \"ч\", \"ъ\", \"ы\", \"ь\", найдено: \"${alphabet[listPosition]}\""
                        )
                    }
                }
                else -> root.addMistake(ORDERED_LIST_INCORRECT_MARKER_FORMAT, pPos)
            }
        } else if (numFormat?.ilvl?.`val` != null && numFormat.ilvl.`val`.toInt() == 1) {
            if (numberingFormat.listLevels["1"]!!.numFmt != NumberFormat.DECIMAL || numberingFormat.listLevels["1"]!!.levelText != "%2)") {
                root.addMistake(ORDERED_LIST_INCORRECT_MARKER_FORMAT_AT_LEVEL_2, pPos, description = "\"<DIGIT>)\"")
            }
        }
    }

    //endregion lists

    private fun unexpectedP(p: Int, something: Any) {
        root.addMistake(PARAGRAPH_UNEXPECTED_CONTENT, p, description = something::class.simpleName!!)
    }

    companion object {
        // todo: maybe char array?
        val orderedListMarkers = "абвгдежиклмнпрстуфхцшщэюя".toList()
        val alphabet = "абвгдежзиклмнопрстуфхцчшщыэюя".toList()

        val pCommonFunctions = listOf(
            Rules.Default.Common.P.hasNotBackground,
            Rules.Default.Common.P.notBordered
        )

        val rCommonFunctions = listOf(
            Rules.Default.Common.R.isTimesNewRoman,
            Rules.Default.Common.R.fontSizeIs14,
            Rules.Default.Common.R.notItalic,
            Rules.Default.Common.R.notCrossedOut,
            Rules.Default.Common.R.notHighlighted,
            Rules.Default.Common.R.isBlack,
            Rules.Default.Common.R.letterSpacingIs0
        )

        val headerRFunctions = listOf(
            Rules.Default.Header.R.isBold,
            Rules.Default.Header.R.isUppercase
        )

        val headerPFunctions = listOf(
            Rules.Default.Header.P.justifyIsCenter,
            Rules.Default.Header.P.lineSpacingIsOne,
            Rules.Default.Header.P.emptyLineAfterHeaderExists,
            Rules.Default.Header.P.hasNotDotInEnd,
            Rules.Default.Header.P.isAutoHyphenSuppressed
        )

        val regularPFunctions = listOf(
            Rules.Default.RegularText.P.leftIndentIs0,
            Rules.Default.RegularText.P.rightIndentIs0,
            Rules.Default.RegularText.P.firstLineIndentIs1dot25,
            Rules.Default.RegularText.P.justifyIsBoth,
            Rules.Default.RegularText.P.lineSpacingIsOneAndHalf
        )

        val regularRFunctions = listOf(
            Rules.Default.RegularText.R.isNotBold,
            Rules.Default.RegularText.R.isNotCaps,
            Rules.Default.RegularText.R.isUnderline
        )
    }
}
*/
