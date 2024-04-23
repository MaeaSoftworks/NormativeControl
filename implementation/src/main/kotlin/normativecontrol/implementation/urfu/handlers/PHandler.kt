package normativecontrol.implementation.urfu.handlers

import normativecontrol.core.annotations.HandlerFactory
import normativecontrol.core.chapters.Chapter
import normativecontrol.core.chapters.ChapterHeader
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.handlers.Factory
import normativecontrol.core.handlers.Handler
import normativecontrol.core.handlers.HandlerMapper
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
import org.docx4j.wml.Lvl
import org.docx4j.wml.NumberFormat
import org.docx4j.wml.P
import org.docx4j.wml.PPrBase.Spacing
import org.docx4j.wml.STLineSpacingRule
import java.math.BigInteger
import kotlin.math.abs

internal class PHandler : Handler<P>(), StateProvider<UrFUState>, ChapterHeader {
    private val headerRegex = Regex("""^(\d+(?:\.\d)*)\s(?:\w\s?)*$""")

    private val rules = Rules()

    var currentText: String? = null
        private set
    var isHeader = false

    private var sinceHeader = -1

    private fun cacheText(element: Any): String {
        if (currentText == null) {
            currentText = TextUtils.getText(element)
        }
        return currentText!!
    }

    inner class ListData {
        var isListElement: Boolean = false
        var isOrdered: Boolean = false
        var listPosition: Int = -1
        var start: Int = -1
        var level: Int = -1
    }

    private val listData = ListData()


    context(VerificationContext)
    override fun handle(element: P) {
        val pPr = element.pPr.resolve()

        if (element.pPr?.sectPr != null) {
            render.pageBreak(-1, createPageStyle(element.pPr.sectPr))
            state.foldStylesheet(render.globalStylesheet)
            state.rSinceBr = 0
        }
        pPr.numberingStyle?.let { handleListElement(element, it) } ?: run { listData.isListElement = false }
        render append p {
            style += {
                marginLeft set (pPr.ind.left verifyBy rules.leftIndent)
                marginRight set (pPr.ind.right verifyBy rules.rightIndent)
                marginBottom set (pPr.spacing?.after verifyBy rules.spacingAfter)
                marginTop set (pPr.spacing?.before verifyBy rules.spacingBefore)
                lineHeight set (pPr.spacing verifyBy rules.spacingLine)?.line
                textIndent set (pPr.ind.firstLine verifyBy rules.firstLineIndent)
                textAlign set pPr.jc?.`val`
                backgroundColor set pPr.shd?.fill
                hyphens set pPr.suppressAutoHyphens?.isVal.let { if (it == true) true else null }
            }
            if (element.content.isEmpty()) {
                addChild(br())
            }
        }
        render.inLastElementScope {
            element.iterate { child, _ ->
                HandlerMapper[configuration, child]?.handleElement(child)
            }
        }
        currentText = null
    }

    context(VerificationContext)
    private fun handleListElement(element: P, lvl: Lvl) {
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
        val text = cacheText(element).trim().uppercase()
        val result = configuration.verificationSettings.chapterConfiguration.headers[text]
        if (result != null) {
            isHeader = true
            sinceHeader = 0
            return result
        }
        if (isChapterBodyHeader(text)) {
            isHeader = true
            sinceHeader = 0
            return Chapters.Body
        }
        if (isAppendixHeader(text)) {
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
        val nextChapters = configuration.verificationSettings.chapterConfiguration.getNextChapters(lastDefinedChapter)
        if (target !in nextChapters) {
            mistake(
                Reason.ChapterOrderMismatch,
                target.names?.joinToString("/"),
                nextChapters.flatMap { configuration.verificationSettings.chapterConfiguration.names[it]!! }.joinToString("/")
            )
        }
        super.checkChapterOrder(target)
    }

    inner class Rules {
        val leftIndent = verifier<BigInteger?> {
            if (currentText!!.isBlank()) return@verifier
            val value = it?.asTwip()?.cm?.round(2) ?: 0.0.cm

            if (isHeader) {
                if (value != 0.0.cm) {
                    return@verifier mistake(Reason.LeftIndentOnHeader)
                }
                return@verifier
            }
            //if (runState.sinceDrawing) {
            //    if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.LeftIndentOnPictureDescription)
            //}
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
            if (currentText!!.isBlank()) return@verifier
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
            if (currentText!!.isBlank()) return@verifier
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
            //if (runState.sinceDrawing) {
            //    if (abs(value - 1.25.cm) <= 0.01.cm)
            //        mistake(Reason.IncorrectFirstLineIndentInHeader, value.double.toString(), "0")
            //}
            if (abs(value - 1.25.cm) >= 0.01.cm)
                return@verifier mistake(Reason.IncorrectFirstLineIndentInText, value.double.toString(), "1.25")
        }

        val spacingBefore = verifier<BigInteger?> {
            return@verifier
        }

        val spacingAfter = verifier<BigInteger?> {
            return@verifier
        }

        val spacingLine = verifier<Spacing?> {
            val line = it?.line?.asPointsToLine() ?: 0.0
            if (isHeader) {
                if (abs(line - 1.0) >= 0.001)
                    mistake(Reason.IncorrectLineSpacingHeader, line.toString(), "1")
                return@verifier
            }
            if (it?.lineRule == STLineSpacingRule.AUTO && abs(line - 1.5) >= 0.001) {
                return@verifier mistake(Reason.IncorrectLineSpacingText, line.toString(), "1.5")
            }
        }
    }

    @HandlerFactory(P::class, UrFUConfiguration::class)
    companion object : Factory<PHandler> {
        override fun create() = PHandler()
    }
}