package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParser
import com.maeasoftworks.normativecontrol.utils.smartAdd
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.PPr

class PFunctionWrapper(val function: (String, Int, PPr, Boolean, MainDocumentPart) -> DocumentError?)

fun Iterable<PFunctionWrapper>.apply(root: DocumentParser, p: Int, pPr: PPr, isEmpty: Boolean) {
    for (wrapper in this) {
        root.errors.smartAdd(wrapper.function(root.document.id, p, pPr, isEmpty, root.mainDocumentPart))
    }
}