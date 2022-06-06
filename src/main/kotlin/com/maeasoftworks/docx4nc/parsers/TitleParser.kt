package com.maeasoftworks.docx4nc.parsers

import com.maeasoftworks.docx4nc.PFunctionWrapper
import com.maeasoftworks.docx4nc.Rules
import com.maeasoftworks.docx4nc.model.Chapter

class TitleParser(chapter: Chapter, root: DocumentParser) : ChapterParser(chapter, root) {
    override fun parse() {
        super.parse(
            this,
            null,
            null,
            PFunctionWrapper.iterable(
                Rules.Default.PictureTitle.P::justifyIsCenter,
                Rules.Default.PictureTitle.P::hasNotDotInEnd
            ) + pCommonFunctions,
            rCommonFunctions
        )
    }
}