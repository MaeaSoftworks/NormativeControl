package normativecontrol.core.implementations.ufru.handlers

import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterHeader
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerConfig
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.abstractions.states.PointerState
import normativecontrol.core.abstractions.states.State
import normativecontrol.core.abstractions.verifier
import normativecontrol.core.abstractions.verifyBy
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.html.br
import normativecontrol.core.html.createPageStyle
import normativecontrol.core.html.p
import normativecontrol.core.implementations.ufru.Chapters
import normativecontrol.core.implementations.ufru.Reason
import normativecontrol.core.implementations.ufru.UrFUProfile
import normativecontrol.core.implementations.ufru.UrFUProfile.globalState
import normativecontrol.core.implementations.ufru.describeState
import normativecontrol.core.math.abs
import normativecontrol.core.math.asPointsToLine
import normativecontrol.core.math.asTwip
import normativecontrol.core.math.cm
import normativecontrol.core.utils.flatMap
import normativecontrol.core.utils.resolvedPPr
import org.docx4j.TextUtils
import org.docx4j.wml.Lvl
import org.docx4j.wml.NumberFormat
import org.docx4j.wml.P
import org.docx4j.wml.PPrBase.Spacing
import org.docx4j.wml.STLineSpacingRule
import org.slf4j.LoggerFactory
import java.math.BigInteger
import kotlin.math.abs

@EagerInitialization
object PHandler : Handler<P, PHandler.PState>(
    HandlerConfig.create {
        setTarget<P>()
        setState(PState) { PState() }
        setHandler { PHandler }
        setProfile(UrFUProfile)
    }
), ChapterHeader {
    private val logger = LoggerFactory.getLogger(this::class.java)
    override val headerRegex = Regex("""^(\d+(?:\.\d)*)\s*.*$""")

    context(VerificationContext)
    override fun handle(element: Any) {
        element as P
        val pPr = element.resolvedPPr

        if (element.pPr?.sectPr != null) {
            render.pageBreak(-1, createPageStyle(element.pPr.sectPr))
            globalState.foldStylesheet(render.globalStylesheet)
            globalState.rSinceBr = 0
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
                pPr.resolvedNumberingStyle?.let { handleListElement(element, it) } ?: run { state.currentListConfig = null }
            }
            if (element.content.isEmpty()) {
                addChild(br())
            }
        }
        render.inLastElementScope {
            element.iterate { child, _ ->
                HandlerMapper[profile, child]?.handle(child)
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
                    if (currentListConfig!!.listPosition !in globalState.referencesInText) {
                        mistake(Reason.ReferenceNotMentionedInText)
                    }
                    return@with
                } else {
                    currentListConfig!!.listPosition++
                    if (currentListConfig!!.listPosition !in globalState.referencesInText) {
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

    context(VerificationContext)
    override fun checkChapterStart(element: Any): Chapter? {
        val text = TextUtils.getText(element as P).trim().uppercase()
        return profile.verificationConfiguration.chapterConfiguration.headers[text] ?: if (isChapterBodyHeader(text)) Chapters.Body else null
    }

    context(VerificationContext)
    override fun checkChapterOrderAndUpdateContext(target: Chapter) {
        val nextChapters = profile.verificationConfiguration.chapterConfiguration.getNextChapters(lastDefinedChapter)
        if (target !in nextChapters) {
            mistake(
                Reason.ChapterOrderMismatch,
                target.names.joinToString("/"),
                nextChapters.flatMap { profile.verificationConfiguration.chapterConfiguration.names[it]!! }.joinToString("/")
            )
        }
        lastDefinedChapter = target
        chapter = target
    }

    object Rules {
        val leftIndent = verifier<BigInteger?> {
            if (isHeader) {
                if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.LeftIndentOnHeader)
            } else {
                if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.LeftIndentOnText)
            }
        }

        val rightIndent = verifier<BigInteger?> {
            if (isHeader) {
                if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.RightIndentOnHeader)
            } else {
                if (it != null && it.asTwip().cm != 0.0.cm) mistake(Reason.RightIndentOnText)
            }
        }

        val firstLineIndent = verifier<BigInteger?> {
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
                PointerState.UnderPicture -> TODO()
                PointerState.PictureDescription -> TODO()
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
                PointerState.UnderPicture -> TODO()
                PointerState.PictureDescription -> TODO()
            }
        }
    }

    class PState : State {
        override val key: State.Key = Companion

        var currentListConfig: ListConfig? = null

        data class ListConfig(
            val isOrdered: Boolean
        ) {
            var listPosition: Int = -1
            var start: Int = -1
        }

        companion object : State.Key
    }
}