package com.maeasoftworks.core.parsers

import com.maeasoftworks.core.enums.MistakeType
import com.maeasoftworks.core.model.Chapter

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

    override fun handleHyperlink(p: Int, r: Int) {
        root.addMistake(MistakeType.TEXT_HYPERLINK_WARNING, p, r)
    }
}
