package com.maeasoftworks.normativecontrol.parser.parsers

import com.maeasoftworks.normativecontrol.parser.PFunctionWrapper
import com.maeasoftworks.normativecontrol.parser.Rules
import com.maeasoftworks.normativecontrol.parser.model.Chapter

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