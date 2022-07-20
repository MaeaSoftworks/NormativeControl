package com.maeasoftworks.polonium.parsers

import com.maeasoftworks.polonium.enums.MistakeType
import com.maeasoftworks.polonium.model.Chapter

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
