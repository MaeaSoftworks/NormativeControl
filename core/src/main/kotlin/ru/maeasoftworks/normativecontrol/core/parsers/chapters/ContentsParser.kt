package ru.maeasoftworks.normativecontrol.core.parsers.chapters

import org.docx4j.wml.P
import org.docx4j.wml.R
import ru.maeasoftworks.normativecontrol.core.model.VerificationContext

data object ContentsParser : ChapterParser {
    override fun parsePHeader(p: P, verificationContext: VerificationContext) {
        TODO("Not yet implemented")
    }

    override fun parsePHeaderR(r: R, verificationContext: VerificationContext) {
        TODO("Not yet implemented")
    }

    override fun parseCommonP(p: P, verificationContext: VerificationContext) {
        TODO("Not yet implemented")
    }

    override fun parseCommonPR(r: R, verificationContext: VerificationContext) {
        TODO("Not yet implemented")
    }
}