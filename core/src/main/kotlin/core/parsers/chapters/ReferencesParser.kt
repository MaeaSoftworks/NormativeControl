package core.parsers.chapters

import core.model.Context
import org.docx4j.wml.P
import org.docx4j.wml.R

data object ReferencesParser : ChapterParser {
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
