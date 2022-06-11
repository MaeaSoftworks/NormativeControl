package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.model.Chapter

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
