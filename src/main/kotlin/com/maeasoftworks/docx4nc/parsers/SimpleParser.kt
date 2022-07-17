package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.enums.MistakeType
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

    override fun handleHyperlink(p: Int, r: Int) {
        root.addMistake(MistakeType.TEXT_HYPERLINK_WARNING, p, r)
    }
}
