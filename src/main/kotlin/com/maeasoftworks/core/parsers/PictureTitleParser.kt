package com.maeasoftworks.core.parsers

import com.maeasoftworks.core.model.Chapter
import com.maeasoftworks.core.model.Rules.Default.PictureTitle.P

class PictureTitleParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    override fun parse() {
        parse(this, null, null, listOf(P.justifyIsCenter, P.hasNotDotInEnd) + pCommonFunctions, rCommonFunctions)
    }
}
