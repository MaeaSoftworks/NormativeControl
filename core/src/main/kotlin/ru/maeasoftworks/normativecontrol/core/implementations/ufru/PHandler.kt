package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.TextUtils
import org.docx4j.wml.P
import ru.maeasoftworks.normativecontrol.core.abstractions.*
import ru.maeasoftworks.normativecontrol.core.annotations.EagerInitialization
import ru.maeasoftworks.normativecontrol.core.enums.Closure
import ru.maeasoftworks.normativecontrol.core.enums.MistakeType
import ru.maeasoftworks.normativecontrol.core.model.Mistake
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext
import ru.maeasoftworks.normativecontrol.core.rendering.br
import ru.maeasoftworks.normativecontrol.core.rendering.p
import ru.maeasoftworks.normativecontrol.core.utils.getPropertyValue
import ru.maeasoftworks.normativecontrol.core.utils.resolvedNumberingStyle

@EagerInitialization
object PHandler : Handler<P>(Profile.UrFU, Mapping.of { PHandler }), ChapterHeader {
    override val headerRegex = Regex("""^(\d+(?:\.\d)?)\s(?:\w+\s?)+$""")

    context(VerificationContext)
    override fun handle(element: Any) {
        element as P
        render.appender append p {
            style += {
                marginLeft set element.pPr.getPropertyValue { ind?.left }?.toDouble()
                marginRight set element.pPr.getPropertyValue { ind?.right }?.toDouble()
                marginBottom set element.pPr.getPropertyValue { spacing?.after }?.toDouble()
                marginTop set element.pPr.getPropertyValue { spacing?.before }?.toDouble()
                lineHeight set element.pPr.getPropertyValue { spacing?.line }?.toDouble()
                textIndent set element.pPr.getPropertyValue { ind?.firstLine }?.toDouble()
                textAlign set element.pPr.getPropertyValue { jc?.`val` }
                backgroundColor set element.pPr.getPropertyValue { shd?.fill }
                hyphens set element.pPr.getPropertyValue { suppressAutoHyphens }?.isVal.let { if (it == true) true else null }
                val numbering = element.pPr?.resolvedNumberingStyle
            }
            if (element.content.isEmpty()) {
                children += br()
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
    override fun isHeader(element: Any): Boolean {
        return (element as P).pPr?.getPropertyValue { outlineLvl }?.`val` != null
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
                    MistakeType.CHAPTER_UNDEFINED_CHAPTER,
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
                        MistakeType.CHAPTER_ORDER_MISMATCH,
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