package com.maeasoftworks.normativecontrol.parser.chapters.parsers

import com.maeasoftworks.normativecontrol.entities.DocumentError
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.RPr

//documentId: String, rPr: RPr, p: Int, r: Int, isEmpty: Boolean, mainDocumentPart: MainDocumentPart
//(String, RPr, Int, Int, Boolean, MainDocumentPart) -> DocumentError?
class RFunctionWrapper(val function: (String, RPr, Int, Int, Boolean, MainDocumentPart) -> DocumentError?)