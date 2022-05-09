package com.maeasoftworks.normativecontrol.dtos.chapters

import com.maeasoftworks.normativecontrol.dtos.Chapter
import com.maeasoftworks.normativecontrol.dtos.DocumentParser

class AppendixParser(parser: DocumentParser, override val chapter: Chapter) : ChapterParser(parser, chapter) {

    override fun parse() {

    }
}