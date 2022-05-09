package com.maeasoftworks.normativecontrol.dtos.chapters

import com.maeasoftworks.normativecontrol.dtos.Chapter
import com.maeasoftworks.normativecontrol.dtos.DocumentParser

class SimpleParser(parser: DocumentParser, override val chapter: Chapter) : ChapterParser(parser, chapter) {

    override fun parse() {
        val paragraphs = chapter.content
        findHeaderPRErrors(chapter.startPos)
        for (paragraph in 1 until paragraphs.size) {
            findRegularTextPRErrors(chapter.startPos + paragraph)
        }
    }
}