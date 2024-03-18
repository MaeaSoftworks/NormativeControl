package normativecontrol.core.implementations.ufru.handlers

import org.docx4j.TextUtils
import org.docx4j.wml.Lvl
import org.docx4j.wml.NumberFormat
import org.docx4j.wml.P
import normativecontrol.core.abstractions.chapters.Chapter
import normativecontrol.core.abstractions.chapters.ChapterHeader
import normativecontrol.core.abstractions.handlers.Handler
import normativecontrol.core.abstractions.handlers.HandlerConfig
import normativecontrol.core.abstractions.handlers.HandlerMapper
import normativecontrol.core.abstractions.states.State
import normativecontrol.core.annotations.EagerInitialization
import normativecontrol.core.contexts.VerificationContext
import normativecontrol.core.implementations.ufru.*
import normativecontrol.core.abstractions.mistakes.Mistake
import normativecontrol.core.html.br
import normativecontrol.core.html.createPageStyle
import normativecontrol.core.html.p
import normativecontrol.core.implementations.ufru.UrFUProfile
import normativecontrol.core.implementations.ufru.UrFUProfile.globalState
import normativecontrol.core.utils.resolvedPPr
import normativecontrol.core.utils.flatMap

@EagerInitialization
object PHandler : Handler<P, PHandler.PState>(
    HandlerConfig.create {
        setTarget<P>()
        setState(PState) { PState() }
        setHandler { PHandler }
        setProfile(UrFUProfile)
    }
), ChapterHeader {
    override val headerRegex = Regex("""^(\d+(?:\.\d)?)\s(?:\w+\s?)+$""")

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
                marginLeft set pPr.ind?.left?.toDouble()
                marginRight set pPr.ind?.right?.toDouble()
                marginBottom set pPr.spacing?.after?.toDouble()
                marginTop set pPr.spacing?.before?.toDouble()
                lineHeight set pPr.spacing?.line?.toDouble()
                textIndent set pPr.ind?.firstLine?.toDouble()
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
            element.iterate { pos ->
                val child = element.content[pos]
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
                addMistake(Mistake(Reason.ForbiddenMarkerTypeReferences))
            }
            with(state) {
                if ((currentListConfig!!.listPosition == -1 && currentListConfig!!.start == -1) || currentListConfig!!.start != lvl.start.`val`.toInt()) {
                    currentListConfig!!.listPosition = lvl.start.`val`.toInt()
                    currentListConfig!!.start = currentListConfig!!.listPosition
                    if (currentListConfig!!.listPosition !in globalState.referencesInText) {
                        addMistake(Mistake(Reason.ReferenceNotMentionedInText))
                    }
                    return@with
                } else {
                    currentListConfig!!.listPosition++
                    if (currentListConfig!!.listPosition !in globalState.referencesInText) {
                        addMistake(Mistake(Reason.ReferenceNotMentionedInText))
                    }
                }
            }
        } else {
            if (lvl.suff?.`val` != "space") {
                addMistake(Mistake(Reason.TabInList))
            }
            if (lvl.ilvl.toInt() == 0) {
                when (lvl.numFmt?.`val`) {
                    NumberFormat.RUSSIAN_LOWER -> {

                    }

                    NumberFormat.BULLET -> {

                    }

                    else -> {
                        addMistake(Mistake(Reason.ForbiddenMarkerTypeLevel1))
                    }
                }
            }
        }
    }

    context(VerificationContext)
    override fun isHeader(element: Any): Boolean {
        return (element as P).resolvedPPr.outlineLvl?.`val` != null
    }

    context(VerificationContext)
    override fun detectChapterByHeader(element: Any): Chapter {
        val text = TextUtils.getText(element)
        if (isChapterBodyHeader(text)) {
            return Chapters.Body
        }
        for (keys in 0 until profile.chapterConfiguration.headers.size) {
            for ((title, chapter) in profile.chapterConfiguration.headers) {
                if (text.uppercase() == title) {
                    return chapter
                }
            }
        }
        if (profile.chapterConfiguration.names[Chapters.Appendix]?.any { text.uppercase().startsWith(it) } == true) {
            return Chapters.Appendix
        }
        return Chapter.Undefined
    }

    context(VerificationContext)
    override fun checkChapterOrderAndUpdateContext(target: Chapter) {
        if (target is Chapter.Undefined) {
            addMistake(
                Mistake(
                    Reason.UndefinedChapterFound,
                    target.names.first(),
                    profile.chapterConfiguration
                        .getPrependChapters(lastDefinedChapter)
                        .flatMap { profile.chapterConfiguration.names[it]!! }
                        .joinToString("/")
                )
            )
        } else {
            if (!profile.chapterConfiguration.getPrependChapters(lastDefinedChapter).contains(target)) {
                addMistake(
                    Mistake(
                        Reason.ChapterOrderMismatch,
                        profile.chapterConfiguration.names[target]!!.joinToString("/"),
                        profile.chapterConfiguration
                            .getPrependChapters(lastDefinedChapter)
                            .flatMap { profile.chapterConfiguration.names[it]!! }
                            .joinToString("/")
                    )
                )
            }
            lastDefinedChapter = target
        }
        chapter = target
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