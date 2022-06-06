package com.maeasoftworks.docx4nc

import com.maeasoftworks.docx4nc.model.MistakeBody
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.PPr

class PFunctionWrapper(val function: (Int, PPr, Boolean, MainDocumentPart) -> MistakeBody?) {
    companion object {
        fun iterable(vararg rules: (Int, PPr, Boolean, MainDocumentPart) -> MistakeBody?): Iterable<PFunctionWrapper> =
            rules.map { PFunctionWrapper(it) }
    }
}

fun Iterable<PFunctionWrapper>.apply(root: DocumentParser, p: Int, pPr: PPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it.function(p, pPr, isEmpty, root.mainDocumentPart)) }
}