package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.handlers.Handler
import normativecontrol.core.chapters.Chapter
import normativecontrol.core.chapters.AbstractChapterHeaderHandler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.AbstractHandler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.math.abs
import normativecontrol.core.math.asPointsToLine
import normativecontrol.core.math.asTwip
import normativecontrol.core.math.cm
import normativecontrol.core.rendering.html.br
import normativecontrol.core.rendering.html.createPageStyle
import normativecontrol.core.rendering.html.p
import normativecontrol.core.components.AbstractTextContentHandler
import normativecontrol.core.traits.ChapterHeaderHandler
import normativecontrol.core.traits.TextContentHandler
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
internal class PHandler : AbstractHandler<P>(), StateProvider<UrFUState>, TextContentHandler, ChapterHeaderHandler {
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
                pageBreak(-1, createPageStyle(element.pPr.sectPr))
                foldStylesheet(globalStylesheet)
                this@PHandler.state.rSinceBr = 0
            }
            pPr.numberingStyle?.let { handleListElement(it) } ?: run { listData.isListElement = false }
            append {
                p {
                    style += {
                        marginLeft set (pPr.ind.left verifyBy rules.leftIndent)
                        marginRight set (pPr.ind.right verifyBy rules.rightIndent)
                        marginBottom set (pPr.spacing.after verifyBy rules.spacingAfter)
                        marginTop set (pPr.spacing.before verifyBy rules.spacingBefore)
                        lineHeight set (pPr.spacing verifyBy rules.spacingLine).line
                        textIndent set (pPr.ind.firstLine verifyBy rules.firstLineIndent)
                        textAlign set (pPr.jc?.`val` verifyBy rules.justifyContent)
                        backgroundColor set (pPr.shd.fill verifyBy rules.backgroundColor)
                        hyphens set pPr.suppressAutoHyphens?.isVal.let { if (it == true) true else null }
                    }
                    if (element.content.isEmpty()) {
                        addChild(br())
                    }
                }
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
    private fun handleListElement(lvl: Lvl) {
        if (!listData.isListElement) {
            listData.isListElement = true
            listData.isOrdered = lvl.numFmt?.`val` == NumberFormat.BULLET
        }

        if (chapter == Chapters.References) {
            if (lvl.numFmt?.`val` != NumberFormat.DECIMAL) {
                return mistake(Reason.ForbiddenMarkerTypeReferences)
            }
            if ((listData.listPosition == -1 && listData.start == -1) || listData.start != lvl.start.`val`.toInt()) {
                listData.listPosition = lvl.start.`val`.toInt()
                listData.start = listData.listPosition
                if (listData.listPosition !in state.referencesInText) {
                    mistake(Reason.ReferenceNotMentionedInText)
                }
            } else {
                listData.listPosition++
                if (listData.listPosition !in state.referencesInText) {
                    mistake(Reason.ReferenceNotMentionedInText)
                }
            }
        } else {
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
            }  else {
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

        val spacingLine = verifier<PPr.Spacing> {
            val line = it.line?.asPointsToLine() ?: 0.0
            return@verifier if (state.isCodeBlock) {
                if (abs(line - 1.0) >= 0.001) mistake(Reason.IncorrectLineSpacingInCode, line.toString(), "1")
                else return@verifier
            } else if (state.isHeader) {
                if (abs(line - 1.0) >= 0.001) mistake(Reason.IncorrectLineSpacingHeader, line.toString(), "1")
                else return@verifier
            } else {
                if (it.lineRule == STLineSpacingRule.AUTO && abs(line - 1.5) >= 0.001)
                    return@verifier mistake(Reason.IncorrectLineSpacingText, line.toString(), "1.5")
                else return@verifier
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

    inner class Text(handler: PHandler): AbstractTextContentHandler(handler) {
        private val inBrackets = """\[(.*?)]""".toRegex()
        private val removePages = """,\s*С\.(?:.*)*""".toRegex()
        private val removeAndMatchRanges = """(\d+)\s*-\s*(\d+)""".toRegex()
        private val matchReference = """(\d+)""".toRegex()

        private val pictureDescription = """^Рисунок (?:[АБВГДЕЖИКЛМНПРСТУФХЦШЩЭЮЯ]\.)?\d+ – .*[^.]$""".toRegex()
        private val tableTitle = """^Таблица (?:[АБВГДЕЖИКЛМНПРСТУФХЦШЩЭЮЯ]\.)?\d+ – .*[^.]$""".toRegex()
        private val tableContinuation = """^Продолжение\sтаблицы\s(?:[АБВГДЕЖИКЛМНПРСТУФХЦШЩЭЮЯ]\.)?\d+[^.]?$""".toRegex()

        override fun defineStateByText() {
            with(ctx) {
                if (chapter.shouldBeVerified) {
                    if (textValue?.startsWith("/**normative*control*code*start**/") == true
                        || textValue?.startsWith("/**c*s**/") == true) {
                        state.isCodeBlock = true
                        state.sinceCodeBlock = 0
                    } else if (textValue?.endsWith("/**normative*control*code*end**/") == true
                        || textValue?.endsWith("/**c*e**/") == true) {
                        hooks.afterHandle.subscribeOnce {
                            state.isCodeBlock = false
                            if (state.sinceCodeBlock > 30) {
                                mistake(Reason.CodeBlockWasTooBig, state.sinceCodeBlock.toString(), "30")
                            }
                        }
                    }
                    if (state.sinceDrawing == 0 && !state.currentPWithDrawing) {
                        if (textValue == null || !pictureDescription.matches(textValue!!)) {
                            mistake(Reason.IncorrectPictureDescriptionPattern)
                        }
                    }
                    if (textValue?.let { tableTitle.matches(it) } == true) {
                        state.tableTitleCounter.reset()
                    } else {
                        state.tableTitleCounter.increment()
                    }
                    if (state.tableCounter.value == 1) {
                        if (textValue != null && tableContinuation.matches(textValue!!)) {
                            state.tableTitleCounter.reset()
                        }
                    }
                }
                state.referencesInText.addAll(getAllReferences(textValue!!))
            }
        }

        private fun getAllReferences(text: String): Set<Int> {
            val set = mutableSetOf<Int>()
            val (refs, ranges) = findAllRanges(clearPages(findAllInBrackets(text))).let { it.first.toList() to it.second }
            ranges.forEach {
                for (i in it) {
                    set += i
                }
            }
            findAllReferences(refs).forEach(set::add)
            return set
        }

        private fun findAllInBrackets(text: String): Sequence<String> {
            return inBrackets.findAll(text).map { it.groups[1]!!.value }
        }

        private fun clearPages(refs: Sequence<String>): Sequence<String> {
            return refs.map { removePages.replace(it, "") }
        }

        private fun findAllRanges(refs: Sequence<String>): Pair<Sequence<String>, List<IntRange>> {
            val ranges = mutableListOf<IntRange>()
            return refs.map {
                val r = removeAndMatchRanges.findAll(it)
                for (matchResult in r) {
                    ranges += matchResult.groups[1]!!.value.toInt()..matchResult.groups[2]!!.value.toInt()
                }
                removeAndMatchRanges.replace(it, "")
            } to ranges
        }

        private fun findAllReferences(refs: List<String>): List<Int> {
            return refs.flatMap { line -> matchReference.findAll(line).map { it.groups[1]!!.value.toInt() } }
        }
    }

    inner class HeaderHandler(handler: AbstractHandler<*>): AbstractChapterHeaderHandler(handler) {
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
                        runtime.handlers[PHandler::class]!!.hooks.afterHandle.subscribe {
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
}