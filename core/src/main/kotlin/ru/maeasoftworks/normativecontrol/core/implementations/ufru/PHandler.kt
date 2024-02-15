package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.TextUtils
import org.docx4j.wml.Lvl
import org.docx4j.wml.NumberFormat
import org.docx4j.wml.P
import ru.maeasoftworks.normativecontrol.core.abstractions.*
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.model.Mistake
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import ru.maeasoftworks.normativecontrol.core.rendering.br
import ru.maeasoftworks.normativecontrol.core.rendering.p
import ru.maeasoftworks.normativecontrol.core.utils.resolvedPPr

@EagerInitialization
object PHandler : Handler<P, PHandler.PState>(
    Config.create {
        setTarget<P>()
        setState(PState) { PState() }
        setHandler { PHandler }
        setProfile(Profile.UrFU)
    }
), ChapterHeader {
    override val headerRegex = Regex("""^(\d+(?:\.\d)?)\s(?:\w+\s?)+$""")

    context(VerificationContext)
    override fun handle(element: Any) {
        element as P
        val pPr = element.resolvedPPr
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
            childLoop { pos ->
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

        if (chapter == ReferencesChapter) {
            // todo references
        } else {
            if (lvl.suff?.`val` != "space") {
                addMistake(Mistake(Reasons.TabInList, Closure.P))
            }
            if (lvl.ilvl.toInt() == 0) {
                when (lvl.numFmt?.`val`) {
                    NumberFormat.RUSSIAN_LOWER -> {

                    }

                    NumberFormat.BULLET -> {

                    }

                    else -> {
                        addMistake(Mistake(Reasons.ForbiddenMarkerTypeLevel1, Closure.P))
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
            return BodyChapter
        }
        for (keys in 0 until profile.chapterConfiguration.headers.size) {
            for ((title, chapter) in profile.chapterConfiguration.headers) {
                if (text.uppercase() == title) {
                    return chapter
                }
            }
        }
        if (profile.chapterConfiguration.names[AppendixChapter]?.any { text.uppercase().startsWith(it) } == true) {
            return AppendixChapter
        }
        return UndefinedChapter
    }

    context(VerificationContext)
    override fun checkChapterOrderAndUpdateContext(target: Chapter) {
        if (target is UndefinedChapter) {
            addMistake(
                Mistake(
                    Reasons.UndefinedChapterFound,
                    Closure.P,
                    profile.chapterConfiguration.names[target]!!.joinToString("/"),
                    profile.chapterConfiguration
                        .getPrependChapter(lastDefinedChapter)
                        .flatMap { profile.chapterConfiguration.names[it]!! }
                        .joinToString("/")
                )
            )
        } else {
            if (!profile.chapterConfiguration.getPrependChapter(lastDefinedChapter).contains(target)) {
                addMistake(
                    Mistake(
                        Reasons.ChapterOrderMismatch,
                        Closure.P,
                        profile.chapterConfiguration.names[target]!!.joinToString("/"),
                        profile.chapterConfiguration
                            .getPrependChapter(lastDefinedChapter)
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

        data class ListConfig(val isOrdered: Boolean)

        companion object : State.Key
    }
}