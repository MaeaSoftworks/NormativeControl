package com.maeasoftworks.normativecontrol.parser

import com.maeasoftworks.normativecontrol.entities.DocumentError
import com.maeasoftworks.normativecontrol.parser.parsers.DocumentParser
import com.maeasoftworks.normativecontrol.utils.smartAdd
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.RPr

class RFunctionWrapper(val function: (String, Int, Int, RPr, Boolean, MainDocumentPart) -> DocumentError?)

fun Iterable<RFunctionWrapper>.apply(root: DocumentParser, p: Int, r: Int, rPr: RPr, isEmpty: Boolean) {
    for (wrapper in this) {
        root.errors.smartAdd(wrapper.function(root.document.id, p, r, rPr, isEmpty, root.mainDocumentPart))
    }
}