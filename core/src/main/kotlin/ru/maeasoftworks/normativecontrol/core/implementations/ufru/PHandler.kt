package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.TextUtils
import org.docx4j.wml.Lvl
import org.docx4j.wml.P
import ru.maeasoftworks.normativecontrol.core.abstractions.*
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.abstractions.Closure
import ru.maeasoftworks.normativecontrol.core.abstractions.MistakeReason
import ru.maeasoftworks.normativecontrol.core.model.Mistake
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import ru.maeasoftworks.normativecontrol.core.rendering.br
import ru.maeasoftworks.normativecontrol.core.rendering.p
import ru.maeasoftworks.normativecontrol.core.utils.*

@EagerInitialization
object PHandler : Handler<P>(Profile.UrFU, Mapping.of { PHandler }), ChapterHeader {
    override val headerRegex = Regex("""^(\d+(?:\.\d)?)\s(?:\w+\s?)+$""")

    context(VerificationContext)
    override fun handle(element: Any) {
        element as P
        val pPr = element.resolvedPPr
        render.appender append p {
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
                pPr.resolvedNumberingStyle?.let { handleListElement(element, it) }
            }
            if (element.content.isEmpty()) {
                addChild(br())
            }
        }
        render.appender.inLastElementScope {
            ptr.childLoop { pos ->
                val child = element.content[pos]
                HandlerMapper[profile, child]?.handle(child)
            }
        }
    }

    context(VerificationContext)
    private fun handleListElement(element: P, lvl: Lvl) {
        if (chapter == ReferencesChapter) {
            // todo references
        } else {
            if (lvl.suff?.`val` != "space") {
                addMistake(Mistake(Reasons.TabInList, Closure.P))
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
                    Reasons.CHAPTER_UNDEFINED_CHAPTER,
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
                        Reasons.CHAPTER_ORDER_MISMATCH,
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
}