package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.model.Chapter

/**
 * Класс, ответственный за парсинг заключения главы
 *
 * @author prmncr
 */class ConclusionParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {

    override fun parse() {}
}
