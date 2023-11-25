package ru.maeasoftworks.normativecontrol.core.parsers.chapters

import org.docx4j.wml.P
import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.model.Context

sealed interface ChapterParser {
    fun parsePHeader(p: P, context: Context)

    fun parsePHeaderR(r: R, context: Context)

    fun parseCommonP(p: P, context: Context)

    fun parseCommonPR(r: R, context: Context)
}
