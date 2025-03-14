package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.chapters.Chapter
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.math.abs
import normativecontrol.core.math.asPointsToLine
import normativecontrol.core.math.asTwip
import normativecontrol.core.math.cm
import normativecontrol.core.predefined.AbstractChapterHeaderTraitImplementor
import normativecontrol.core.predefined.AbstractTextContentTraitImplementor
import normativecontrol.core.predefined.ChapterHeaderTrait
import normativecontrol.core.predefined.TextContentTrait
import normativecontrol.core.rendering.css.DeclarationBlock
import normativecontrol.core.rendering.html.Pages
import normativecontrol.core.rendering.html.br
import normativecontrol.core.rendering.html.p
import normativecontrol.core.utils.flatMap
import normativecontrol.core.verifier
import normativecontrol.core.verifyBy
import normativecontrol.core.wrappers.PPr
import normativecontrol.core.wrappers.PPr.Companion.resolve
import normativecontrol.implementation.urfu.Chapters
import normativecontrol.implementation.urfu.Reason
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.wml.*
import java.math.BigInteger
import kotlin.math.abs

@Handler(P::class, UrFUConfiguration::class)
internal class PHandler : AbstractHandler<P>(), StateProvider<UrFUState>, TextContentTrait, ChapterHeaderTrait {
    private val headerRegex = Regex("""^(\d+(?:\.\d)*)\s(?:\S\s?)*$""")

    private val rules = Rules()

    private var sinceHeader = -1
    private val listData = ListData()
    override val text = Text(this)
    override val chapterHeaderHandler = HeaderHandler(this)

    context(VerificationContext)
    override fun handle(element: P) {
        val pPr = element.pPr.resolve()

        render {
            if (element.pPr?.sectPr != null) {
                pageBreak(-1, Pages.createPageStyle(element.pPr.sectPr))
                joinStylesheet(globalStylesheet)
                state.rSinceBr = 0
            }

            if (pPr.numberingStyle == null) {
                listData.isListElement = false
            } else {
                if (chapter != Chapters.References) {
                    handleListElement(pPr.numberingStyle!!)
                } else {
                    verifyReferenceParagraph(pPr.numberingStyle!!)
                }
            }

            val declarationBlock = DeclarationBlock.detached {
                marginLeft set (pPr.ind.left verifyBy rules.leftIndent)
                marginRight set (pPr.ind.right verifyBy rules.rightIndent)
                marginBottom set (pPr.spacing.after verifyBy rules.spacingAfter)
                marginTop set (pPr.spacing.before verifyBy rules.spacingBefore)
                lineHeight set Pair(pPr.spacing.line, pPr.spacing.lineRule)
                pPr.spacing verifyBy rules.spacingLine
                textIndent set (pPr.ind.firstLine verifyBy rules.firstLineIndent)
                textAlign set (pPr.jc?.`val` verifyBy rules.justifyContent)
                backgroundColor set (pPr.shd.fill verifyBy rules.backgroundColor)
                hyphens set pPr.suppressAutoHyphens?.isVal.let { if (it == true) true else null }
            }

            val renderedElement = p {}

            renderedElement.style.apply(declarationBlock)

            if (element.content.isEmpty()) {
                renderedElement.addChild(br())
            }

            append {
                renderedElement
            }

            inLastElementScope {
                element.iterate { child, _ ->
                    runtime.handlers[child]?.handleElement(child)
                }
            }
        }
        state.sinceCodeBlock++
    }

    context(VerificationContext)
    private fun verifyReferenceParagraph(lvl: Lvl) {
        if (!listData.isListElement) {
            listData.isListElement = true
            listData.isOrdered = lvl.numFmt?.`val` == NumberFormat.BULLET
        }
        if (lvl.numFmt?.`val` != NumberFormat.DECIMAL) {
            return mistake(Reason.ForbiddenMarkerTypeReferences)
        }
        if ((listData.listPosition == -1 && listData.start == -1) || listData.start != lvl.start.`val`.toInt()) {
            listData.listPosition = lvl.start.`val`.toInt()
            listData.start = listData.listPosition
        } else {
            listData.listPosition++
        }
        if (listData.listPosition !in state.referencesInText) {
            mistake(Reason.ReferenceNotMentionedInText, force = true)
        }
    }

    context(VerificationContext)
    private fun handleListElement(lvl: Lvl) {
        if (!listData.isListElement) {
            listData.isListElement = true
            listData.isOrdered = lvl.numFmt?.`val` == NumberFormat.BULLET
        }

        listData.level = lvl.ilvl.toInt()
        if (lvl.suff?.`val` != "space") {
            mistake(Reason.TabInList)
        }
        if (lvl.ilvl.toInt() == 0) {
            when (lvl.numFmt?.`val`) {
                NumberFormat.RUSSIAN_LOWER -> {}

                NumberFormat.BULLET -> {}

                NumberFormat.DECIMAL -> {}

                else -> {
                    mistake(Reason.ForbiddenMarkerTypeLevel1)
                }
            }
        }
    }

    private fun isChapterBodyHeader(text: String): Boolean {
        return text.matches(headerRegex)
    }

    private fun isAppendixHeader(text: String): Boolean {
        return Chapters.Appendix.prefixes?.any { text.startsWith(it) } == true
    }

    private inner class Rules {
        private val isPictureTitle: Boolean
            get() = with(runtime.context) { state.sinceDrawing == 0 && !state.currentPWithDrawing }

        val leftIndent = verifier<BigInteger?> {
            if (text.isBlank != false) return@verifier
            val value = it?.asTwip()?.cm?.round(2) ?: 0.0.cm
            return@verifier if (state.isCodeBlock) {
                if (value != 0.0.cm) {
                    mistake(Reason.LeftIndentOnCode)
                } else return@verifier
            } else if (state.isHeader) {
                if (value != 0.0.cm) {
                    mistake(Reason.LeftIndentOnHeader)
                } else return@verifier
            } else if (isPictureTitle) {
                if (it != null && it.asTwip().cm != 0.0.cm) {
                    mistake(Reason.LeftIndentOnPictureDescription)
                } else return@verifier
            } else if (listData.isListElement) {
                val expected = 0.75.cm * (listData.level)
                if (value != expected) {
                    mistake(
                        Reason.IncorrectLeftIndentInList,
                        value.value.toString(),
                        expected.value.toString()
                    )
                } else return@verifier
            } else if (value != 0.0.cm) {
                mistake(Reason.LeftIndentOnText)
            } else return@verifier
        }

        val rightIndent = verifier<BigInteger?> {
            if (text.isBlank != false) return@verifier
            val value = it?.asTwip()?.cm ?: 0.0.cm
            return@verifier if (state.isCodeBlock) {
                if (value != 0.0.cm) {
                    mistake(Reason.RightIndentOnCode)
                } else return@verifier
            } else if (state.isHeader) {
                if (value != 0.0.cm) {
                    mistake(Reason.RightIndentOnHeader)
                } else return@verifier
            } else {
                if (value != 0.0.cm) {
                    mistake(Reason.RightIndentOnText)
                } else return@verifier
            }
        }

        val firstLineIndent = verifier<BigInteger?> {
            if (text.isBlank != false) return@verifier
            val value = it?.asTwip()?.cm ?: 0.0.cm
            return@verifier if (state.isCodeBlock) {
                if (value >= 0.01.cm)
                    mistake(Reason.FirstLineIndentOnCode, value.value.toString(), "0")
                else return@verifier
            } else if (state.isHeader) {
                when (chapter) {
                    Chapters.Body -> {
                        if (abs(value - 1.25.cm) >= 0.01.cm) {
                            mistake(Reason.IncorrectFirstLineIndentInHeader, value.value.toString(), "1.25")
                        } else return@verifier
                    }

                    else -> {
                        if (abs(value - 1.25.cm) <= 0.01.cm) {
                            return@verifier mistake(Reason.IncorrectFirstLineIndentInHeader, value.value.toString(), "0")
                        } else return@verifier
                    }
                }
            } else if (isPictureTitle) {
                if (value >= 0.01.cm)
                    mistake(Reason.IncorrectFirstLineIndentInPictureDescription, value.value.toString(), "0")
                else return@verifier
            } else if (state.tableTitleCounter.isReset) {
                if (value >= 0.01.cm)
                    mistake(Reason.IncorrectFirstLineIndentInTableTitle, value.value.toString(), "0")
                else return@verifier
            } else {
                if (abs(value - 1.25.cm) >= 0.01.cm)
                    mistake(Reason.IncorrectFirstLineIndentInText, value.value.toString(), "1.25")
                else return@verifier
            }
        }

        val spacingBefore = verifier<BigInteger?> {
            if (it != null && it.asTwip().cm != 0.0.cm) {
                mistake(Reason.SpacingBefore)
            }
        }

        val spacingAfter = verifier<BigInteger?> {
            if (it != null && it.asTwip().cm != 0.0.cm) {
                mistake(Reason.SpacingAfter)
            }
        }

        val spacingLine = verifier<PPr.Spacing> { l ->
            val line = l.line?.asPointsToLine() ?: 0.0
            return@verifier if (state.isCodeBlock) {
                if (l.lineRule != STLineSpacingRule.AUTO || abs(line - 1.0) >= 0.001)
                    mistake(Reason.IncorrectLineSpacingInCode, line.toString(), "1")
                else return@verifier
            } else if (state.isHeader) {
                if (l.lineRule != STLineSpacingRule.AUTO || abs(line - 1.5) >= 0.001)
                    mistake(Reason.IncorrectLineSpacingHeader, line.toString(), "1.5")
                else return@verifier
            } else {
                if (sinceHeader == 1) {
                    events.beforeHandle.subscribeOnce {
                        if (state.isHeader) { // line after header and before header
                            if (l.lineRule != STLineSpacingRule.AUTO || abs(line - 1.0) >= 0.001)
                                mistake(Reason.IncorrectLineSpacingBetweenHeaders, line.toString(), "1")
                        }
                    }
                } else if (l.lineRule == STLineSpacingRule.AUTO && abs(line - 1.5) >= 0.001) {
                    return@verifier mistake(Reason.IncorrectLineSpacingText, line.toString(), "1.5")
                } else return@verifier
            }
        }

        val justifyContent = verifier<JcEnumeration?> {
            if (text.isBlank != false) return@verifier
            return@verifier if (state.isCodeBlock) {
                if (it != JcEnumeration.LEFT) {
                    mistake(Reason.JustifyOnCode)
                } else return@verifier
            } else if (state.isHeader) {
                if (chapter == Chapters.Body) {
                    if (it != JcEnumeration.BOTH) mistake(Reason.IncorrectJustifyOnBodyHeader)
                    else return@verifier
                } else {
                    if (it != JcEnumeration.CENTER) mistake(Reason.IncorrectJustifyOnHeader)
                    else return@verifier
                }
            } else if (isPictureTitle) {
                if (it != JcEnumeration.CENTER) mistake(Reason.IncorrectJustifyOnPictureDescription)
                else return@verifier
            } else if (state.tableTitleCounter.isReset) {
                if (it != JcEnumeration.LEFT) mistake(Reason.IncorrectJustifyOnTableTitle)
                else return@verifier
            } else {
                if (it != JcEnumeration.BOTH) mistake(Reason.IncorrectJustifyOnText)
                else return@verifier
            }
        }

        val backgroundColor = verifier<String?> {
            if (it != null && it != "FFFFFF") {
                mistake(Reason.BackgroundColor)
            }
        }
    }

    inner class ListData {
        var isListElement: Boolean = false
        var isOrdered: Boolean = false
        var listPosition: Int = -1
        var start: Int = -1
        var level: Int = -1
    }

    inner class Text(handler: PHandler) : AbstractTextContentTraitImplementor(handler) {
        override fun defineStateByText(): Unit = with(ctx) {
            if (chapter.shouldBeVerified) {
                searchCodeBlockContext()
                    ?: searchDrawingContext()
                    ?: searchTableContext()
                    ?: searchTableContinuationContext()
            }
            state.referencesInText.addAll(getAllReferences(textValue!!))
        }

        context(VerificationContext)
        private fun searchCodeBlockContext(): Unit? {
            if (textValue?.startsWith("/**normative*control*code*start**/") == true || textValue?.startsWith("/**c*s**/") == true) {
                state.isCodeBlock = true
                state.sinceCodeBlock = 0
                return Unit
            } else if (textValue?.endsWith("/**normative*control*code*end**/") == true || textValue?.endsWith("/**c*e**/") == true) {
                events.afterHandle.subscribeOnce {
                    state.isCodeBlock = false
                    if (state.sinceCodeBlock > 30) {
                        mistake(Reason.CodeBlockWasTooBig, state.sinceCodeBlock.toString(), "30")
                    }
                }
                return Unit
            }
            return null
        }

        context(VerificationContext)
        private fun searchDrawingContext(): Unit? {
            if (state.sinceDrawing == 0 && !state.currentPWithDrawing) {
                if (text.textValue == null || !pictureDescriptionRegex.matches(text.textValue!!)) {
                    mistake(Reason.IncorrectPictureDescriptionPattern)
                }
                return Unit
            }
            return null
        }

        context(VerificationContext)
        private fun searchTableContext(): Unit? {
            if (textValue?.let { tableTitleRegex.matches(it) } == true) {
                state.tableTitleCounter.reset()
                return Unit
            } else {
                state.tableTitleCounter.increment()
                return null
            }
        }

        context(VerificationContext)
        private fun searchTableContinuationContext(): Unit? {
            if (state.sinceLastTableCounter.value == 1) {
                if (textValue != null && tableContinuationRegex.matches(textValue!!)) {
                    state.tableTitleCounter.reset()
                    return Unit
                }
            }
            return null
        }

        private fun getAllReferences(text: String): Set<Int> {
            val intRefs = mutableSetOf<Int>()
            val refs = squareBracketsRegex.findAll(text)
                .map {
                    val refWithoutPages = pageInRefRegex.replace(it.groups[1]!!.value, "")
                    for (matchResult in intRangeRegex.findAll(refWithoutPages)) {
                        intRefs.addAll(matchResult.groups[1]!!.value.toInt()..matchResult.groups[2]!!.value.toInt())
                    }
                    intRangeRegex.replace(refWithoutPages, "")
                }
            refs.flatMap { line -> simpleIntRefRegex.findAll(line).map { it.groups[1]!!.value.toInt() } }.forEach(intRefs::add)
            return intRefs
        }
    }

    inner class HeaderHandler(handler: AbstractHandler<*>) : AbstractChapterHeaderTraitImplementor(handler) {
        context(VerificationContext)
        override fun checkChapterStart(element: Any): Chapter? {
            if (state.suppressChapterRecognition && state.sinceSdtBlock <= 0) {
                state.isHeader = false
                sinceHeader++
                return null
            }
            if (state.forceLegacyHeaderSearch && state.sinceSdtBlock > -1) {
                if ((element as P).pPr.resolve().outlineLvl?.`val`?.toInt() == null) {
                    state.isHeader = false
                    sinceHeader++
                    return null
                }
            }
            val uppercaseText = text.cacheText(element).trim().uppercase()
            val result = configuration.verificationSettings.chapterConfiguration.headers[uppercaseText]
            if (result != null) {
                if (state.forceLegacyHeaderSearch) {
                    state.forceLegacyHeaderSearch = false
                }
                state.isHeader = true
                sinceHeader = 0
                if (result == Chapters.Contents) {
                    if (!state.inSdtBlock) {
                        mistake(Reason.ContentsNotInSdtBlock, force = true)
                        runtime.handlers[PHandler::class]!!.events.afterHandle.subscribe {
                            state.sinceSdtBlock++
                            state.forceLegacyHeaderSearch = true
                            if (!state.noSdtBlockReported && state.sinceSdtBlock >= 10) {
                                mistake(Reason.ContentsSdtBlockNotFound, force = true)
                                state.noSdtBlockReported = true
                            }
                        }
                    } else {
                        state.suppressChapterRecognition = true
                    }
                }
                return result
            }
            if (chapter == Chapters.NO_DETECT_BODY) return null
            if (isChapterBodyHeader(uppercaseText)) {
                state.isHeader = true
                sinceHeader = 0
                return Chapters.Body
            }
            if (isAppendixHeader(uppercaseText)) {
                state.isHeader = true
                sinceHeader = 0
                return Chapters.Appendix
            }
            state.isHeader = false
            sinceHeader++
            return null
        }

        context(VerificationContext)
        override fun checkChapterOrder(target: Chapter) {
            val nextChapters = configuration.verificationSettings.chapterConfiguration.getNextChapters(chapter)
            if (target !in nextChapters) {
                mistake(
                    Reason.ChapterOrderMismatch,
                    target.names?.joinToString("/"),
                    nextChapters.flatMap { configuration.verificationSettings.chapterConfiguration.names[it]!! }.joinToString("/")
                )
            }
            chapter = target
        }
    }

    companion object {
        private val squareBracketsRegex = """\[(.*?)]""".toRegex()
        private val pageInRefRegex = """,\s*С\.(?:.*)*""".toRegex()
        private val intRangeRegex = """(\d+)\s*-\s*(\d+)""".toRegex()
        private val simpleIntRefRegex = """(\d+)""".toRegex()

        private val pictureDescriptionRegex = """^Рисунок (?:[АБВГДЕЖИКЛМНПРСТУФХЦШЩЭЮЯ]\.)?\d+ – .*[^.]$""".toRegex()
        private val tableTitleRegex = """^Таблица (?:[АБВГДЕЖИКЛМНПРСТУФХЦШЩЭЮЯ]\.)?\d+ – .*[^.]$""".toRegex()
        private val tableContinuationRegex = """^Продолжение\sтаблицы\s(?:[АБВГДЕЖИКЛМНПРСТУФХЦШЩЭЮЯ]\.)?\d+[^.]?$""".toRegex()
    }
}