package core.parsers.chapters

import core.model.Context
import org.docx4j.wml.P
import org.docx4j.wml.R

sealed interface ChapterParser {
    fun parsePHeader(p: P, context: Context)

    fun parsePHeaderR(r: R, context: Context)

    fun parseCommonP(p: P, context: Context)

    fun parseCommonPR(r: R, context: Context)
}