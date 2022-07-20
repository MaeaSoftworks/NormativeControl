package com.maeasoftworks.polonium.parsers

import com.maeasoftworks.polonium.model.Chapter

/**
 * Class for creating anonymous objects of parser in code
 *
 * @author prmncr
 */
open class AnonymousParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    constructor(root: DocumentParser) : this(Chapter(0), root)

    override fun parse() {}
}
