package com.maeasoftworks.core.parsers

import com.maeasoftworks.core.model.Chapter

/**
 * Class for creating anonymous objects of parser in code
 */
open class AnonymousParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    constructor(root: DocumentParser) : this(Chapter(0), root)

    override fun parse() {}
}
