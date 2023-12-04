package ru.maeasoftworks.normativecontrol.core.model

import org.docx4j.TextUtils
import org.docx4j.wml.P
import ru.maeasoftworks.normativecontrol.core.enums.CaptureType
import ru.maeasoftworks.normativecontrol.core.enums.MistakeType
import ru.maeasoftworks.normativecontrol.core.parsers.chapters.AppendixParser
import ru.maeasoftworks.normativecontrol.core.parsers.chapters.BodyParser
import ru.maeasoftworks.normativecontrol.core.parsers.chapters.ChapterParser
import ru.maeasoftworks.normativecontrol.core.parsers.chapters.UndefinedChapter
import ru.maeasoftworks.normativecontrol.core.utils.getPropertyValue
import ru.maeasoftworks.normativecontrol.core.utils.usingContext
import kotlin.coroutines.coroutineContext

object PVerifier: Verifier<P> {
    override suspend fun verify(child: P) {
        val lvl = child.getPropertyValue { outlineLvl }
        if (lvl?.`val` != null) {
            detectChapterByHeader(child, lvl.`val`.intValueExact())
        }
    }

    private suspend fun detectChapterByHeader(p: P, level: Int) {
        val text = TextUtils.getText(p)
        if (text.matches(Regex("^(\\d+(?:\\.\\d*)?).*\$"))) {
            checkChapterOrderAndSet(BodyParser)
            return
        }
        for (keys in 0 until ChapterMarkers.markers.size) {
            for ((title, parser) in ChapterMarkers.markers) {
                if (text.uppercase() == title) {
                    checkChapterOrderAndSet(parser)
                    return
                }
            }
        }
        if (text.uppercase().startsWith(ChapterMarkers.APPENDIX_NAME)) {
            checkChapterOrderAndSet(AppendixParser)
            return
        }
        checkChapterOrderAndSet(UndefinedChapter)
    }

    private suspend fun checkChapterOrderAndSet(chapterParser: ChapterParser) = usingContext { ctx ->
        if (chapterParser is UndefinedChapter) {
            ctx.addMistake(
                Mistake(
                    MistakeType.CHAPTER_UNDEFINED_CHAPTER,
                    CaptureType.P,
                    ChapterMarkers.names[chapterParser]!!.joinToString("/"),
                    ChapterMarkers.nextOf[ctx.lastDefinedChapter]!!.flatMap { ChapterMarkers.names[it]!! }.joinToString("/")
                )
            )
        } else {
            if (!ChapterMarkers.nextOf[ctx.lastDefinedChapter]!!.contains(chapterParser)) {
                ctx.addMistake(
                    Mistake(
                        MistakeType.CHAPTER_ORDER_MISMATCH,
                        CaptureType.P,
                        ChapterMarkers.names[chapterParser]!!.joinToString("/"),
                        ChapterMarkers.nextOf[ctx.lastDefinedChapter]!!.flatMap { ChapterMarkers.names[it]!! }.joinToString("/")
                    )
                )
            }
            ctx.lastDefinedChapter = chapterParser
        }
        ctx.chapter = chapterParser
    }
}