package com.maeasoftworks.polonium.parsers

import com.maeasoftworks.polonium.model.Chapter

/**
 * Класс для обработки списка
 *
 * @param chapter глава, в которой находится список
 * @param root объект DocumentParser, Обрабатывающий документ. в котором находится список
 *
 * @author prmncr
 */
class ListParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    override fun parse() {
        parse(
            this,
            null,
            null,
            pCommonFunctions + regularPFunctions,
            null
        )
    }
}
