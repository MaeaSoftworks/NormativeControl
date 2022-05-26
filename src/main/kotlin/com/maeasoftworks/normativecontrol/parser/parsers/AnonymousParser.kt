package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.parser.model.Chapter

open class AnonymousParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    constructor(root: DocumentParser) : this(Chapter(0), root)

    override fun parse() {}
}