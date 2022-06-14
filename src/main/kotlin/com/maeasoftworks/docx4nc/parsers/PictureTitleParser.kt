package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.model.Chapter
import com.maeasoftworks.docx4nc.model.Rules.Default.PictureTitle.P

class PictureTitleParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    override fun parse() {
        super.parse(
            this,
            null,
            null,
            listOf(P::justifyIsCenter, P::hasNotDotInEnd) + pCommonFunctions,
            rCommonFunctions
        )
    }
}
