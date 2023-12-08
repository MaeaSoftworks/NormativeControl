package ru.maeasoftworks.normativecontrol.core.implementations.ufru

import org.docx4j.TextUtils
import org.docx4j.wml.P
import ru.maeasoftworks.normativecontrol.core.enums.CaptureType
import ru.maeasoftworks.normativecontrol.core.enums.MistakeType
import ru.maeasoftworks.normativecontrol.core.abstractions.Chapter
import ru.maeasoftworks.normativecontrol.core.model.ChapterConfiguration
import ru.maeasoftworks.normativecontrol.core.model.Mistake
import ru.maeasoftworks.normativecontrol.core.utils.getPropertyValue
import ru.maeasoftworks.normativecontrol.core.utils.usingContext

object PVerifier: FullVerifier<P> {
    override suspend fun verify(element: P) {
        val lvl = element.getPropertyValue { outlineLvl }
        if (lvl?.`val` != null) {
            detectChapterByHeader(element, lvl.`val`.intValueExact())
        }
    }

    override suspend fun verifyForAnnotation(element: P) {

    }

    override suspend fun verifyForAppendix(element: P) {

    }

    override suspend fun verifyForBody(element: P) {

    }

    override suspend fun verifyForConclusion(element: P) {

    }

    override suspend fun verifyForContents(element: P) {

    }

    override suspend fun verifyForFrontPage(element: P) {

    }

    override suspend fun verifyForIntroduction(element: P) {

    }

    override fun verifyForReferences(element: P) {

    }

    private fun isChapterBodyHeader(text: String): Boolean {
        return text.matches(Regex("""^(\d+(?:\.\d)?)\s(?:\w+\s?)+$"""))
    }

    private suspend fun detectChapterByHeader(p: P, level: Int) {
        val text = TextUtils.getText(p)
        if (isChapterBodyHeader(text)) {
            checkChapterOrderAndSet(BodyVerifier)
            return
        }
        for (keys in 0 until ChapterConfiguration.headers.size) {
            for ((title, parser) in ChapterConfiguration.headers) {
                if (text.uppercase() == title) {
                    checkChapterOrderAndSet(parser)
                    return
                }
            }
        }
        if (text.uppercase().startsWith(ChapterConfiguration.APPENDIX_NAME)) {
            checkChapterOrderAndSet(AppendixVerifier)
            return
        }
        checkChapterOrderAndSet(UndefinedVerified)
    }

    private suspend fun checkChapterOrderAndSet(chapter: Chapter.Companion) = usingContext { ctx ->
        if (chapter is UndefinedVerified.Companion) {
            ctx.addMistake(
                Mistake(
                    MistakeType.CHAPTER_UNDEFINED_CHAPTER,
                    CaptureType.P,
                    ChapterConfiguration.names[chapter]!!.joinToString("/"),
                    ChapterConfiguration.getPrependChapter(ctx.lastDefinedChapter).flatMap { ChapterConfiguration.names[it]!! }.joinToString("/")
                )
            )
        } else {
            if (!ChapterConfiguration.getPrependChapter(ctx.lastDefinedChapter).contains(chapter)) {
                ctx.addMistake(
                    Mistake(
                        MistakeType.CHAPTER_ORDER_MISMATCH,
                        CaptureType.P,
                        ChapterConfiguration.names[chapter]!!.joinToString("/"),
                        ChapterConfiguration.getPrependChapter(ctx.lastDefinedChapter).flatMap { ChapterConfiguration.names[it]!! }.joinToString("/")
                    )
                )
            }
            ctx.lastDefinedChapter = chapter
        }
        ctx.chapter = chapter
    }
}