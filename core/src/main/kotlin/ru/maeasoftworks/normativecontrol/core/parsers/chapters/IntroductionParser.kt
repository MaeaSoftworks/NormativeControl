package ru.maeasoftworks.normativecontrol.core.parsers.chapters

import org.docx4j.wml.P
import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.model.Context

data object IntroductionParser : ChapterParser {
    override fun parsePHeader(p: P, context: Context) {
        TODO("Not yet implemented")
    }

    override fun parsePHeaderR(r: R, context: Context) {
        TODO("Not yet implemented")
    }

    override fun parseCommonP(p: P, context: Context) {
        TODO("Not yet implemented")
    }

    override fun parseCommonPR(r: R, context: Context) {
        TODO("Not yet implemented")
    }
}