package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.model.Chapter

open class SimpleParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {

    override fun parse() {
        parse(
            this,
            headerPFunctions + pCommonFunctions,
            headerRFunctions + rCommonFunctions,
            pCommonFunctions + regularPFunctions,
            rCommonFunctions + regularRFunctions
        )
    }
}