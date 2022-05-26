package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.parser.model.Chapter

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