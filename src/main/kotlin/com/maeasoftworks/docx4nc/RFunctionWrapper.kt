package com.maeasoftworks.docx4nc

import com.maeasoftworks.docx4nc.model.MistakeBody
import com.maeasoftworks.docx4nc.parsers.DocumentParser
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.RPr

class RFunctionWrapper(val function: (Int, Int, RPr, Boolean, MainDocumentPart) -> MistakeBody?) {
    companion object {
        fun iterable(vararg rules: (Int, Int, RPr, Boolean, MainDocumentPart) -> MistakeBody?): Iterable<RFunctionWrapper> =
            rules.map { RFunctionWrapper(it) }
    }
}

fun Iterable<RFunctionWrapper>.apply(root: DocumentParser, p: Int, r: Int, rPr: RPr, isEmpty: Boolean) {
    this.forEach { root.addMistake(it.function(p, r, rPr, isEmpty, root.mainDocumentPart)) }
}