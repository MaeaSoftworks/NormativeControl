package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.entities.Mistake
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParser
import com.maeasoftworks.normativecontrol.utils.smartAdd
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.PPr

class PFunctionWrapper(val function: (String, Int, PPr, Boolean, MainDocumentPart) -> Mistake?) {
    companion object {
        fun iterable(vararg rules: (String, Int, PPr, Boolean, MainDocumentPart) -> Mistake?): Iterable<PFunctionWrapper> =
            rules.map { PFunctionWrapper(it) }
    }
}

fun Iterable<PFunctionWrapper>.apply(root: DocumentParser, p: Int, pPr: PPr, isEmpty: Boolean) {
    this.forEach { root.errors.smartAdd(it.function(root.document.id, p, pPr, isEmpty, root.mainDocumentPart)) }
}