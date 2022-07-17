package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.model.Chapter

/**
 * Класс, используемый для создания парсеров во время работы, без создания специального классса-парсера
 *
 * @author prmncr
 */
open class AnonymousParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    constructor(root: DocumentParser) : this(Chapter(0), root)

    override fun parse() {}
}
