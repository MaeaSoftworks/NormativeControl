package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParser
import com.maeasoftworks.normativecontrol.utils.smartAdd
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.RPr

class RFunctionWrapper(val function: (String, Int, Int, RPr, Boolean, MainDocumentPart) -> DocumentError?) {
    companion object {
        fun iterable(vararg rules: (String, Int, Int, RPr, Boolean, MainDocumentPart) -> DocumentError?): Iterable<RFunctionWrapper> = rules.map { RFunctionWrapper(it) }
    }
}

fun Iterable<RFunctionWrapper>.apply(root: DocumentParser, p: Int, r: Int, rPr: RPr, isEmpty: Boolean) {
    this.forEach { root.errors.smartAdd(it.function(root.document.id, p, r, rPr, isEmpty, root.mainDocumentPart)) }
}