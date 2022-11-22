package com.maeasoftworks.polonium.parsers

import com.maeasoftworks.polonium.model.Chapter
import com.maeasoftworks.polonium.model.Rules.Default.PictureTitle.P

class PictureTitleParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    override fun parse() {
        parse(this, null, null, listOf(P.justifyIsCenter, P.hasNotDotInEnd) + pCommonFunctions, rCommonFunctions)
    }
}
