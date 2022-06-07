package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.model.Chapter

open class AnonymousParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    constructor(root: DocumentParser) : this(Chapter(0), root)

    override fun parse() {}
}
