package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.TextUtils
import org.docx4j.wml.P
import ru.maeasoftworks.normativecontrol.core.abstractions.*
import ru.maeasoftworks.normativecontrol.core.enums.Closure
import ru.maeasoftworks.normativecontrol.core.enums.MistakeType
import ru.maeasoftworks.normativecontrol.core.model.Mistake
import ru.maeasoftworks.normativecontrol.core.rendering.model.html.br
import ru.maeasoftworks.normativecontrol.core.rendering.model.html.p
import ru.maeasoftworks.normativecontrol.core.utils.getPropertyValue
import ru.maeasoftworks.normativecontrol.core.utils.usingContext
import me.prmncr.hotloader.HotLoaded

@HotLoaded
object PHandler : Handler<P>({ register<P>(Profile.UrFU) { PHandler } }), ChapterHeader {
    override val headerRegex = Regex("""^(\d+(?:\.\d)?)\s(?:\w+\s?)+$""")

    override suspend fun handle(element: Any) = usingContext {
        element as P
        render.body.children += p {
            style {
                marginLeft `=` element.getPropertyValue { ind?.left }?.toDouble()
                marginRight `=` element.getPropertyValue { ind?.right }?.toDouble()
                marginBottom `=` element.getPropertyValue { spacing?.after }?.toDouble()
                marginTop `=` element.getPropertyValue { spacing?.before }?.toDouble()
                lineHeight `=` element.getPropertyValue { spacing?.line }?.toDouble()
                textIndent `=` element.getPropertyValue { ind?.firstLine }?.toDouble()
                textAlign `=` element.getPropertyValue { jc?.`val` }
                backgroundColor `=` element.getPropertyValue { shd?.fill }
                hyphens `=` !(element.getPropertyValue { suppressAutoHyphens?.isVal } ?: false)
            }
            if (element.content.isEmpty()) {
                children += br()
            }
            this@usingContext.ptr.childLoop { pos ->
                val child = element.content[pos]
                HandlerMapper[this@usingContext.profile, child]?.handle(child)
            }
        }


        /*
        if (currentPage == null) {
            currentPage = newPage
            html.children.add(currentPage!!)
        }
        currentPage!!.children.add(currentP!!)
        currentP!!.id = p.paraId
        stylizeP(p)
        if (p.content.isEmpty()) {
            currentP!!.children.add(HtmlElement("br", false))
        }
        for (r in p.content.indices) {
            when (p.content[r]) {
                is R -> renderR(p.content[r] as R)
                is JAXBElement<*> -> {
                    when ((p.content[r] as JAXBElement<*>).value) {
                        is P.Hyperlink -> {
                            isInner = true
                            renderHyperlink((p.content[r] as JAXBElement<*>).value as P.Hyperlink)
                        }
                    }
                }
            }
        }
        */
    }

    override suspend fun isHeader(element: Any): Boolean {
        return (element as P).getPropertyValue { outlineLvl }?.`val` != null
    }

    override suspend fun detectChapterByHeader(element: Any): Chapter = usingContext {
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

    override suspend fun checkChapterOrderAndUpdateContext(chapter: Chapter) = usingContext {
        if (chapter is UndefinedChapter) {
            addMistake(
                Mistake(
                    MistakeType.CHAPTER_UNDEFINED_CHAPTER,
                    Closure.P,
                    profile.chapterConfiguration.names[chapter]!!.joinToString("/"),
                    profile.chapterConfiguration
                        .getPrependChapter(lastDefinedChapter)
                        .flatMap { profile.chapterConfiguration.names[it]!! }
                        .joinToString("/")
                )
            )
        } else {
            if (!profile.chapterConfiguration.getPrependChapter(lastDefinedChapter).contains(chapter)) {
                addMistake(
                    Mistake(
                        MistakeType.CHAPTER_ORDER_MISMATCH,
                        Closure.P,
                        profile.chapterConfiguration.names[chapter]!!.joinToString("/"),
                        profile.chapterConfiguration
                            .getPrependChapter(lastDefinedChapter)
                            .flatMap { profile.chapterConfiguration.names[it]!! }
                            .joinToString("/")
                    )
                )
            }
            lastDefinedChapter = chapter
        }
        this.chapter = chapter
    }
}