package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.chapters.Chapter
import normativecontrol.core.chapters.ChapterHeader
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.StateProvider
import normativecontrol.core.math.abs
import normativecontrol.core.math.asPointsToLine
import normativecontrol.core.math.asTwip
import normativecontrol.core.math.cm
import normativecontrol.core.rendering.html.br
import normativecontrol.core.rendering.html.createPageStyle
import normativecontrol.core.rendering.html.p
import normativecontrol.core.utils.flatMap
import normativecontrol.core.verifier
import normativecontrol.core.verifyBy
import normativecontrol.core.wrappers.PPr.Companion.resolve
import normativecontrol.implementation.urfu.Chapters
import normativecontrol.implementation.urfu.Reason
import normativecontrol.implementation.urfu.UrFUConfiguration
import normativecontrol.implementation.urfu.UrFUState
import org.docx4j.TextUtils
import org.docx4j.wml.*
import org.docx4j.wml.PPrBase.Spacing
import java.math.BigInteger
import kotlin.math.abs

internal class PHandler : Handler<P>(), StateProvider<UrFUState>, ChapterHeader {
    private val headerRegex = Regex("""^(\d+(?:\.\d)*)\s(?:\S\s?)*$""")

    private val rules = Rules()

    var isHeader = false

    private var sinceHeader = -1

    inner class ListData {
        var isListElement: Boolean = false
        var isOrdered: Boolean = false
        var listPosition: Int = -1
        var start: Int = -1
        var level: Int = -1
    }
    private val listData = ListData()

    inner class Text {
        var value: String? = null
        var isBlank: Boolean? = null

        private val inBrackets = """\[(.*?)]""".toRegex()
        private val removePages = """,\s*С\.(?:.*)*""".toRegex()
        private val removeAndMatchRanges = """(\d+)\s*-\s*(\d+)""".toRegex()
        private val matchReference = """(\d+)""".toRegex()

        context(VerificationContext)
        fun cacheText(element: Any): String {
            if (value == null) {
                value = TextUtils.getText(element)
                isBlank = value!!.isBlank()
                state.referencesInText.addAll(getAllReferences(value!!))
            }
            return value!!
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
    private val text = Text()

    context(VerificationContext)
    override fun handle(element: P) {
        val pPr = element.pPr.resolve()

        if (element.pPr?.sectPr != null) {
            render.pageBreak(-1, createPageStyle(element.pPr.sectPr))
            state.foldStylesheet(render.globalStylesheet)
            state.rSinceBr = 0
        }
        pPr.numberingStyle?.let { handleListElement(it) } ?: run { listData.isListElement = false }
        render append p {
            style += {
                marginLeft set (pPr.ind.left verifyBy rules.leftIndent)
                marginRight set (pPr.ind.right verifyBy rules.rightIndent)
                marginBottom set (pPr.spacing?.after verifyBy rules.spacingAfter)
                marginTop set (pPr.spacing?.before verifyBy rules.spacingBefore)
                lineHeight set (pPr.spacing verifyBy rules.spacingLine)?.line
                textIndent set (pPr.ind.firstLine verifyBy rules.firstLineIndent)
                textAlign set (pPr.jc?.`val` verifyBy rules.justifyContent)
                backgroundColor set (pPr.shd.fill verifyBy rules.backgroundColor)
                hyphens set pPr.suppressAutoHyphens?.isVal.let { if (it == true) true else null }
            }
            if (element.content.isEmpty()) {
                addChild(br())
            }
        }
        render.inLastElementScope {
            element.iterate { child, _ ->
                runtime.getHandlerFor(child)?.handleElement(child)
            }
        }
        text.value = null
        text.isBlank = null
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
                    NumberFormat.RUSSIAN_LOWER -> {

                    }

                    NumberFormat.BULLET -> {

                    }

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

    context(VerificationContext)
    override fun checkChapterStart(element: Any): Chapter? {
        val uppercaseText = text.cacheText(element).trim().uppercase()
        val result = configuration.verificationSettings.chapterConfiguration.headers[uppercaseText]
        if (result != null) {
            isHeader = true
            sinceHeader = 0
            return result
        }
        if (isChapterBodyHeader(uppercaseText)) {
            isHeader = true
            sinceHeader = 0
            return Chapters.Body
        }
        if (isAppendixHeader(uppercaseText)) {
            isHeader = true
            sinceHeader = 0
            return Chapters.Appendix
        }
        isHeader = false
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

    inner class Rules {
        private val isPictureTitle: Boolean
            get() = with(runtime.context) { state.sinceDrawing == 0 && !state.currentPWithDrawing }

        val leftIndent = verifier<BigInteger?> {
            if (text.isBlank != false) return@verifier
            val value = it?.asTwip()?.cm?.round(2) ?: 0.0.cm

            if (isHeader) {
                if (value != 0.0.cm) {
                    return@verifier mistake(Reason.LeftIndentOnHeader)
                }
                return@verifier
            }
            if (isPictureTitle) {
                if (it != null && it.asTwip().cm != 0.0.cm) return@verifier mistake(Reason.LeftIndentOnPictureDescription)
                return@verifier
            }
            if (listData.isListElement) {
                val expected = 0.75.cm * (listData.level)
                if (value != expected) {
                    return@verifier mistake(
                        Reason.IncorrectLeftIndentInList,
                        value.double.toString(),
                        expected.double.toString()
                    )
                }
                return@verifier
            }
            if (value != 0.0.cm) {
                return@verifier mistake(Reason.LeftIndentOnText)
            }
        }

        val rightIndent = verifier<BigInteger?> {
            if (text.isBlank != false) return@verifier
            if (isHeader) {
                if (it != null && it.asTwip().cm != 0.0.cm) {
                    return@verifier mistake(Reason.RightIndentOnHeader)
                }
                return@verifier
            } else {
                if (it != null && it.asTwip().cm != 0.0.cm) {
                    return@verifier mistake(Reason.RightIndentOnText)
                }
                return@verifier
            }
        }

        val firstLineIndent = verifier<BigInteger?> {
            if (text.isBlank != false) return@verifier
            val value = it?.asTwip()?.cm ?: 0.0.cm
            if (isHeader) {
                when (chapter) {
                    Chapters.Body -> {
                        if (abs(value - 1.25.cm) >= 0.01.cm) {
                            return@verifier mistake(Reason.IncorrectFirstLineIndentInHeader, value.double.toString(), "1.25")
                        }
                        return@verifier
                    }

                    else -> {
                        if (abs(value - 1.25.cm) <= 0.01.cm) {
                            return@verifier mistake(Reason.IncorrectFirstLineIndentInHeader, value.double.toString(), "0")
                        }
                        return@verifier
                    }
                }
            }
            if (isPictureTitle) {
                if (value >= 0.01.cm)
                    return@verifier mistake(Reason.IncorrectFirstLineIndentInPictureDescription, value.double.toString(), "0")
                return@verifier
            }
            if (abs(value - 1.25.cm) >= 0.01.cm)
                return@verifier mistake(Reason.IncorrectFirstLineIndentInText, value.double.toString(), "1.25")
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

        val spacingLine = verifier<Spacing?> {
            val line = it?.line?.asPointsToLine() ?: 0.0
            return@verifier if (isHeader) {
                if (abs(line - 1.0) >= 0.001) mistake(Reason.IncorrectLineSpacingHeader, line.toString(), "1")
                else return@verifier
            } else {
                if (it?.lineRule == STLineSpacingRule.AUTO && abs(line - 1.5) >= 0.001)
                    return@verifier mistake(Reason.IncorrectLineSpacingText, line.toString(), "1.5")
                else return@verifier
            }
        }

        val justifyContent = verifier<JcEnumeration?> {
            if (text.isBlank != false) return@verifier
            return@verifier if (isHeader) {
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

    @HandlerFactory(P::class, UrFUConfiguration::class)
    companion object : Factory<PHandler> {
        override fun create() = PHandler()
    }
}