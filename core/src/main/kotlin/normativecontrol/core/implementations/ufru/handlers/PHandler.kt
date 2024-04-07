package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterHeader
import normativecontrol.core.abstractions.handlers.StatefulHandler
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.implementations.ufru.utils.PointerState
import normativecontrol.core.abstractions.states.State
import normativecontrol.core.abstractions.states.StateFactory
import normativecontrol.core.abstractions.verifier
import normativecontrol.core.abstractions.verifyBy
import normativecontrol.core.annotations.ReflectHandler
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.rendering.html.br
import normativecontrol.core.rendering.html.createPageStyle
import normativecontrol.core.rendering.html.p
import normativecontrol.core.implementations.ufru.Chapters
import normativecontrol.core.implementations.ufru.Reason
import normativecontrol.core.implementations.ufru.UrFUConfiguration
import normativecontrol.core.implementations.ufru.UrFUConfiguration.state as runState
import normativecontrol.core.implementations.ufru.utils.describeState
import normativecontrol.core.math.abs
import normativecontrol.core.math.asPointsToLine
import normativecontrol.core.math.asTwip
import normativecontrol.core.math.cm
import normativecontrol.core.utils.flatMap
import normativecontrol.core.wrappers.resolvedPPr
import org.docx4j.TextUtils
import org.docx4j.wml.Lvl
import org.docx4j.wml.NumberFormat
import org.docx4j.wml.P
import org.docx4j.wml.PPrBase.Spacing
import org.docx4j.wml.STLineSpacingRule
import java.math.BigInteger
import kotlin.math.abs

@ReflectHandler(P::class, UrFUConfiguration::class)
object PHandler : StatefulHandler<P, PHandler.PState>, ChapterHeader {
    private val headerRegex = Regex("""^(\d+(?:\.\d)*)\s*.*$""")

    override var stateFactory: StateFactory = PState

    context(VerificationContext)
    override fun handle(element: P) {
        val pPr = element.resolvedPPr

        if (element.pPr?.sectPr != null) {
            render.pageBreak(-1, createPageStyle(element.pPr.sectPr))
            runState.foldStylesheet(render.globalStylesheet)
            runState.rSinceBr = 0
        }
        render append p {
            style += {
                marginLeft set (pPr.ind?.left verifyBy Rules.leftIndent)
                marginRight set (pPr.ind?.right verifyBy Rules.rightIndent)
                marginBottom set (pPr.spacing?.after verifyBy Rules.spacingAfter)
                marginTop set (pPr.spacing?.before verifyBy Rules.spacingBefore)
                lineHeight set (pPr.spacing verifyBy Rules.spacingLine)?.line
                textIndent set (pPr.ind?.firstLine verifyBy Rules.firstLineIndent)
                textAlign set pPr.jc?.`val`
                backgroundColor set pPr.shd?.fill
                hyphens set pPr.suppressAutoHyphens?.isVal.let { if (it == true) true else null }
            }
            if (element.content.isEmpty()) {
                addChild(br())
            }
        }
        pPr.resolvedNumberingStyle?.let { handleListElement(element, it) } ?: run { state.currentListConfig = null }
        render.inLastElementScope {
            element.iterate { child, _ ->
                HandlerMapper[configuration, child]?.handleElement(child)
            }
        }
    }

    context(VerificationContext)
    private fun handleListElement(element: P, lvl: Lvl) {
        with(state) {
            if (currentListConfig == null) {
                currentListConfig = PState.ListConfig(lvl.numFmt?.`val` == NumberFormat.BULLET)
            }
        }

        if (chapter == Chapters.References) {
            if (lvl.numFmt?.`val` != NumberFormat.DECIMAL) {
                mistake(Reason.ForbiddenMarkerTypeReferences)
            }
            with(state) {
                if ((currentListConfig!!.listPosition == -1 && currentListConfig!!.start == -1) || currentListConfig!!.start != lvl.start.`val`.toInt()) {
                    currentListConfig!!.listPosition = lvl.start.`val`.toInt()
                    currentListConfig!!.start = currentListConfig!!.listPosition
                    if (currentListConfig!!.listPosition !in runState.referencesInText) {
                        mistake(Reason.ReferenceNotMentionedInText)
                    }
                    return@with
                } else {
                    currentListConfig!!.listPosition++
                    if (currentListConfig!!.listPosition !in runState.referencesInText) {
                        mistake(Reason.ReferenceNotMentionedInText)
                    }
                }
            }
        } else {
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
        val text = state.getText(element).trim().uppercase()
        val result = configuration.verificationSettings.chapterConfiguration.headers[text]
        if (result != null) return result
        if (isChapterBodyHeader(text)) {
           return Chapters.Body
        }
        if (isAppendixHeader(text)) {
            return Chapters.Appendix
        }
        return null
    }

    context(VerificationContext)
    override fun checkChapterOrderAndUpdateContext(target: Chapter) {
        val nextChapters = configuration.verificationSettings.chapterConfiguration.getNextChapters(lastDefinedChapter)
        if (target !in nextChapters) {
            mistake(
                Reason.ChapterOrderMismatch,
                target.names?.joinToString("/"),
                nextChapters.flatMap { configuration.verificationSettings.chapterConfiguration.names[it]!! }.joinToString("/")
            )
        }
        lastDefinedChapter = target
        chapter = target
    }

    object Rules {
        val leftIndent = verifier<BigInteger?> {
            if (state.currentText!!.isBlank()) return@verifier
            when(describeState()) {
                PointerState.Header -> {
                    if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.LeftIndentOnHeader)
                }
                PointerState.UnderHeader -> {
                    return@verifier
                }
                PointerState.Text -> {
                    if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.LeftIndentOnText)
                }
                PointerState.PictureDescription -> {
                    if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.LeftIndentOnPictureDescription)
                }
            }
        }

        val rightIndent = verifier<BigInteger?> {
            if (state.currentText!!.isBlank()) return@verifier
            if (isHeader) {
                if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.RightIndentOnHeader)
            } else {
                if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.RightIndentOnText)
            }
        }

        val firstLineIndent = verifier<BigInteger?> {
            if (state.currentText!!.isBlank()) return@verifier
            val value = it?.asTwip()?.cm ?: 0.0.cm
            when (describeState()) {
                PointerState.Header -> {
                    when (chapter) {
                        Chapters.Body -> {
                            if (abs(value - 1.25.cm) >= 0.01.cm)
                                mistake(Reason.IncorrectFirstLineIndentInHeader, value.double.toString(), "1.25")
                        }
                        else -> {
                            if (abs(value - 1.25.cm) <= 0.01.cm)
                                mistake(Reason.IncorrectFirstLineIndentInHeader, value.double.toString(), "0")
                        }
                    }
                }
                PointerState.Text -> {
                    if (abs(value - 1.25.cm) >= 0.01.cm)
                        mistake(Reason.IncorrectFirstLineIndentInText, value.double.toString(), "1.25")
                }
                PointerState.PictureDescription -> {
                    if (abs(value - 1.25.cm) <= 0.01.cm)
                        mistake(Reason.IncorrectFirstLineIndentInHeader, value.double.toString(), "0")
                }
                else -> Unit
            }
        }

        val spacingBefore = verifier<BigInteger?> {

        }

        val spacingAfter = verifier<BigInteger?> {

        }

        val spacingLine = verifier<Spacing?> {
            val line = it?.line?.asPointsToLine() ?: 0.0
            when (describeState()) {
                PointerState.Header -> {
                    if (abs(line - 1.0) >= 0.001)
                        mistake(Reason.IncorrectLineSpacingHeader, line.toString(), "1")
                }
                PointerState.UnderHeader,
                PointerState.Text -> {
                    if (it?.lineRule == STLineSpacingRule.AUTO && abs(line - 1.5) >= 0.001)
                        mistake(Reason.IncorrectLineSpacingText, line.toString(), "1.5")
                }
                PointerState.PictureDescription -> TODO()
            }
        }
    }

    class PState : State {
        override val key = Companion

        var currentListConfig: ListConfig? = null
        var currentText: String? = null
            private set

        override fun reset() {
            currentText = null
        }

        fun getText(element: Any): String {
            if (currentText == null) {
                currentText = TextUtils.getText(element)
            }
            return currentText!!
        }

        data class ListConfig(
            val isOrdered: Boolean
        ) {
            var listPosition: Int = -1
            var start: Int = -1
        }

        companion object : StateFactory {
            override fun createState(): State {
                return PState()
            }
        }
    }
}