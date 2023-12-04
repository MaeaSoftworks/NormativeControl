package ru.maeasoftworks.normativecontrol.core.parsers.chapters

import org.docx4j.wml.P
import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

sealed interface ChapterParser {
    fun parsePHeader(p: P, verificationContext: VerificationContext)

    fun parsePHeaderR(r: R, verificationContext: VerificationContext)

    fun parseCommonP(p: P, verificationContext: VerificationContext)

    fun parseCommonPR(r: R, verificationContext: VerificationContext)
}